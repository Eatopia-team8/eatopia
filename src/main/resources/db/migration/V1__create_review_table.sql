CREATE TABLE review (
                        id BIGINT NOT NULL AUTO_INCREMENT,
                        user_id BIGINT NOT NULL,
                        product_id BIGINT NOT NULL,
                        rating INT NOT NULL,
                        status VARCHAR(255) NOT NULL,
                        deleted_at DATETIME(6),
                        created_at DATETIME(6),
                        updated_at DATETIME(6),
                        PRIMARY KEY (id)
);