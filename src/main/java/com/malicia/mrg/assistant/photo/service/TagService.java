package com.malicia.mrg.assistant.photo.service;

import com.malicia.mrg.assistant.photo.MyConfig;
import com.malicia.mrg.assistant.photo.pojo.TagNode;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TagService {

    private final MyConfig config;
    private final List<TagNode> rootTags;

    public TagService(MyConfig config) throws Exception {
        this.config = config;
        rootTags = config.getTagsList();
    }

    public List<TagNode> getRootTags() {
        return rootTags;
    }

    public TagNode getTagById(Long id) {
        return findByIdRecursive(rootTags, id);
    }

    private TagNode findByIdRecursive(List<TagNode> nodes, Long id) {
        for (TagNode node : nodes) {
            if (node.getId().equals(id)) {
                return node;
            }
            TagNode found = findByIdRecursive(node.getChildren(), id);
            if (found != null) return found;
        }
        return null;
    }

    public TagNode getTagByName(String name) {
        return findByNameRecursive(rootTags, name.toLowerCase());
    }

    private TagNode findByNameRecursive(List<TagNode> nodes, String targetName) {
        for (TagNode node : nodes) {
            if (node.getName() != null && node.getName().equalsIgnoreCase(targetName)) {
                return node;
            }
            TagNode found = findByNameRecursive(node.getChildren(), targetName);
            if (found != null) return found;
        }
        return null;
    }


    public List<String> getTagListByName(String name) {

        List<String> tagListPossible = new ArrayList<>();
        for (TagNode node : rootTags) {
            if (node.getName() != null && node.getName().equalsIgnoreCase(name)) {
                for (TagNode nodeChildren : node.getChildren()) {
                    tagListPossible.add(nodeChildren.getName().toLowerCase());
                }
            }
        }

        return tagListPossible;
    }

}
