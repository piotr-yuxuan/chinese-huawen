-- -----------------------------------------------------
-- Schema huawen
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `huawen` ;
CREATE SCHEMA IF NOT EXISTS `huawen` DEFAULT CHARACTER SET utf8 ;
USE `huawen` ;

-- -----------------------------------------------------
-- Table `huawen`.`ideogram`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `huawen`.`ideogram` ;

CREATE TABLE IF NOT EXISTS `huawen`.`ideogram` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `code_point` VARCHAR(45) NOT NULL,
  `grapheme` CHAR,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `huawen`.`structure`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `huawen`.`structure` ;

CREATE TABLE IF NOT EXISTS `huawen`.`structure` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45),
  `ideographic_description_character` VARCHAR(45),
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `huawen`.`ideogram_structure`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `huawen`.`ideogram_structure` ;

CREATE TABLE IF NOT EXISTS `huawen`.`ideogram_structure` (
  `structure_id` INT(11) NOT NULL,
  `ideogram_id` INT(11) NOT NULL,
  `version` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`structure_id`, `ideogram_id`),
  INDEX `ideogram_id` (`ideogram_id` ASC),
  CONSTRAINT `ideogram_structure_ibfk_1`
    FOREIGN KEY (`structure_id`)
    REFERENCES `huawen`.`structure` (`id`),
  CONSTRAINT `ideogram_structure_ibfk_2`
    FOREIGN KEY (`ideogram_id`)
    REFERENCES `huawen`.`ideogram` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `huawen`.`ideogram_decomposition`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `huawen`.`ideogram_decomposition` ;

CREATE TABLE IF NOT EXISTS `huawen`.`ideogram_decomposition` (
  `father_ideogram_id` INT(11) NOT NULL,
  `structure_id` INT(11) NOT NULL,
  `ideogram_id` INT(11) NOT NULL,
  `ordinal` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`father_ideogram_id`, `structure_id`, `ideogram_id`),
  INDEX `ideogram_id` (`ideogram_id` ASC),
  CONSTRAINT `ideogram_decomposition_ibfk_1`
    FOREIGN KEY (`ideogram_id`)
    REFERENCES `huawen`.`ideogram` (`id`),
  CONSTRAINT `ideogram_decomposition_ibfk_2`
    FOREIGN KEY (`father_ideogram_id` , `structure_id`)
    REFERENCES `huawen`.`ideogram_structure` (`ideogram_id` , `structure_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;
