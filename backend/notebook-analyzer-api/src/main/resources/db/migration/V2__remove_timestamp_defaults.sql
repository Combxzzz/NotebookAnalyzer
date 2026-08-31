ALTER TABLE notebooks
    ALTER COLUMN created_at DROP DEFAULT;

ALTER TABLE notebook_collections
    ALTER COLUMN received_at DROP DEFAULT;