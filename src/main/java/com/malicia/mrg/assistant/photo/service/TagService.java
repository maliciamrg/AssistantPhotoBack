package com.malicia.mrg.assistant.photo.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.malicia.mrg.assistant.photo.MyConfig;
import com.malicia.mrg.assistant.photo.dto.TagNodeDto;
import com.malicia.mrg.assistant.photo.entity.TagNode;
import com.malicia.mrg.assistant.photo.exception.CustomException;
import com.malicia.mrg.assistant.photo.repository.TagNodeRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.*;

@Service
public class TagService {
    private static final Logger logger = LoggerFactory.getLogger(RootRepertoire.class);
    private final TagNodeRepository tagNodeRepository;
    private final MyConfig myConfig;

    public TagService(TagNodeRepository tagNodeRepository, MyConfig myConfig) throws Exception {
        this.tagNodeRepository = tagNodeRepository;
        this.myConfig = myConfig;
    }

    @PostConstruct
    public void loadFlatJsonToDb() {
        Path inputPath = Paths.get(myConfig.getRootPath(), myConfig.getTagFileName());

        if (Files.exists(inputPath)) {
            try (InputStream is = Files.newInputStream(inputPath)) {
                List<TagNodeDto> tagNodeList = new ObjectMapper().readValue(is, new TypeReference<List<TagNodeDto>>() {
                });

                tagNodeRepository.deleteAll();

                // First pass: create all TagNodes without parents
                Map<Long, TagNode> tagNodeMap = new HashMap<>();

                for (TagNodeDto dto : tagNodeList) {
                    TagNode tag = new TagNode();
                    tag.setId(dto.getId());
                    tag.setName(dto.getName());
                    tagNodeMap.put(tag.getId(), tag);
                }

                tagNodeRepository.saveAll(tagNodeMap.values());
                tagNodeRepository.flush();

                // Second pass: assign parents
                for (TagNodeDto dto : tagNodeList) {
                    TagNode tag = tagNodeMap.get(dto.getId());
                    if (dto.getParentId() != null) {
                        TagNode parent = tagNodeMap.get(dto.getParentId());
                        tag.setParent(parent);
                    }
                }

                // Save all at once
                //tagNodeRepository.flush();
                tagNodeRepository.saveAll(tagNodeMap.values());

            } catch (IOException e) {
                throw new RuntimeException("Failed to load JSON", e);
            }
        }
    }

    public List<TagNode> getRootTags() {
        List<TagNode> tagNodeRepositoryAll = tagNodeRepository.findAll();
        try {
            exportTagsToFile(tagNodeRepositoryAll);
        } catch (IOException e) {
            throw new CustomException(e);
        }
        return tagNodeRepositoryAll;
    }

    public void exportTagsToFile(List<TagNode> tags) throws IOException {
        if (tags == null || tags.isEmpty()) {
            logger.warn("No tags to export.");
            return;
        }

        List<TagNodeDto> tagsDto = new ArrayList<>();
        for (TagNode tag : tags) {
            tagsDto.add(new TagNodeDto(tag));
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT); // Optional: pretty print

        Path outputPath = Paths.get(myConfig.getRootPath(), myConfig.getTagFileName());

        // Ensure the directory exists
        Files.createDirectories(outputPath.getParent());

        mapper.writeValue(outputPath.toFile(), tagsDto);

        logger.debug("Tags exported to: {}", outputPath.toAbsolutePath());
    }

    public TagNode getTagById(Long id) {
        return tagNodeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tag not found with id: " + id));
    }

    public TagNode getTagByName(String name) {
        return tagNodeRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new RuntimeException("Tag not found with name: " + name));
    }


    public List<String> getTagListByName(String name) {
        return tagNodeRepository.findDirectChildrenNamesByParentName(name);
    }

    public TagNode createTag(TagNodeDto dto) {
        TagNode tag = new TagNode();
        tag.setId(dto.getId());
        tag.setName(normalizeTagName(dto.getName()));

        if (dto.getParentId() != null) {
            TagNode parent = tagNodeRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent not found"));
            tag.setParent(parent);
        }

        return tagNodeRepository.save(tag);
    }

    public TagNode updateTag(Long id, TagNodeDto dto) {
        TagNode tag = tagNodeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tag not found"));

        tag.setName(normalizeTagName(dto.getName()));

        if (dto.getParentId() != null) {
            TagNode parent = tagNodeRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent not found"));
            tag.setParent(parent);
        } else {
            tag.setParent(null);
        }

        return tagNodeRepository.save(tag);
    }

    public void deleteTag(Long id) {
        if (!tagNodeRepository.existsById(id)) {
            throw new RuntimeException("Tag not found");
        }
        tagNodeRepository.deleteById(id);
    }

    public int updateTagName(Long id, String name) {
        return tagNodeRepository.updateTagNameById(id, normalizeTagName(name));
    }

    public Long getNextFreeTagId() {
        List<Long> existingIds = tagNodeRepository.findAllIds(); // Custom query method
        if (existingIds.isEmpty()) {
            return 1L;
        }

        Set<Long> idSet = new HashSet<>(existingIds);
        Long nextId = 1L;

        while (idSet.contains(nextId)) {
            nextId++;
        }

        return nextId;
    }

    @Transactional
    public void normalizeAllTagNames() {
        List<TagNode> tags = tagNodeRepository.findAll();

        for (TagNode tag : tags) {
            String original = tag.getName();
            String cleaned = normalizeTagName(original);

            if (!cleaned.equals(original)) {
                tag.setName(cleaned);
            }
        }

        tagNodeRepository.saveAll(tags);
    }

    public String normalizeTagName(String input) {
        if (input == null) return null;

        // Remove leading/trailing spaces
        String result = input.trim();

        // Convert to lowercase
        result = result.toLowerCase();

        // Remove accents
        result = Normalizer.normalize(result, Normalizer.Form.NFD);
        result = result.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        // Replace spaces with underscores
        result = result.replace(" ", "-");

        // Replace - with underscores
        result = result.replace("_", "-");

        return result;
    }
}
