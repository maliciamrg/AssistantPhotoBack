CREATE TABLE photo
(
    id               UUID NOT NULL,
    path             VARCHAR(255),
    thumbnail        BYTEA,
    relative_to_path VARCHAR(255),
    filename         VARCHAR(255),
    extension        VARCHAR(255),
    created_date     VARCHAR(255),
    exif_date        VARCHAR(255),
    date_taken       VARCHAR(255),
    flagged          BOOLEAN DEFAULT FALSE,    -- Flagged field (boolean)
    flag_type        VARCHAR(255) DEFAULT NULL,             -- Flag type (e.g., 'pick')
    starred          INTEGER DEFAULT 0,         -- Starred field (integer)
    CONSTRAINT pk_photo PRIMARY KEY (id),
    CONSTRAINT uq_path_filename UNIQUE (path, filename),
    CONSTRAINT chk_flag_type CHECK (flag_type IN ('reject', 'pick', NULL)),  -- Check constraint on flag_type
    CONSTRAINT chk_starred CHECK (starred BETWEEN 0 AND 5)  -- Check constraint on starred (0-5)
);
