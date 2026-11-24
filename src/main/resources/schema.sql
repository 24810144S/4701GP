-- CIS School Admission System | MySQL 8.0.16+
-- Run once against the existing, empty development database.
USE `4701gp_dev`;

CREATE TABLE accounts (
    id BIGINT NOT NULL AUTO_INCREMENT,
    email VARCHAR(254) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    `role` VARCHAR(10) COLLATE utf8mb4_bin NOT NULL,
    full_name VARCHAR(100) DEFAULT NULL,
    hkid VARCHAR(20) DEFAULT NULL,
    phone VARCHAR(30) DEFAULT NULL,
    verified_at DATETIME DEFAULT NULL,
    verification_code_hash VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_accounts_email (email),
    CONSTRAINT chk_accounts_role CHECK (`role` IN ('MEMBER', 'ADMIN'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE programmes (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    application_deadline DATETIME NOT NULL COMMENT 'Hong Kong local time',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE programme_levels (
    id BIGINT NOT NULL AUTO_INCREMENT,
    programme_id BIGINT NOT NULL,
    `level` TINYINT NOT NULL,
    seat_limit INT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_programme_levels_programme_level (programme_id, `level`),
    CONSTRAINT fk_programme_levels_programme FOREIGN KEY (programme_id)
        REFERENCES programmes (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT chk_programme_levels_level CHECK (`level` BETWEEN 1 AND 6),
    CONSTRAINT chk_programme_levels_seat_limit CHECK (seat_limit >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE applications (
    id BIGINT NOT NULL AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    programme_level_id BIGINT NOT NULL,
    submitted_at DATETIME NOT NULL,
    decision VARCHAR(10) COLLATE utf8mb4_bin DEFAULT NULL,
    published_at DATETIME DEFAULT NULL,
    offer_response VARCHAR(10) COLLATE utf8mb4_bin DEFAULT NULL,
    responded_at DATETIME DEFAULT NULL,
    PRIMARY KEY (id),
    KEY ix_applications_member (member_id),
    KEY ix_applications_level_decision (programme_level_id, decision),
    CONSTRAINT fk_applications_member FOREIGN KEY (member_id)
        REFERENCES accounts (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_applications_programme_level FOREIGN KEY (programme_level_id)
        REFERENCES programme_levels (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT chk_applications_decision CHECK (decision IN ('SUCCESS', 'FAIL', 'WAIT')),
    CONSTRAINT chk_applications_response CHECK (offer_response IN ('ACCEPTED', 'DECLINED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
