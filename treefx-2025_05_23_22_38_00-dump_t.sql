/*M!999999\- enable the sandbox mode */ 
-- MariaDB dump 10.19  Distrib 10.11.11-MariaDB, for Linux (x86_64)
--
-- Host: 127.0.0.1    Database: treefx
-- ------------------------------------------------------
-- Server version	10.11.11-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `node`
--

DROP TABLE IF EXISTS `node`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `node` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `imgURL` varchar(200) NOT NULL,
  `position` point DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=153 DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `node`
--

LOCK TABLES `node` WRITE;
/*!40000 ALTER TABLE `node` DISABLE KEYS */;
INSERT INTO `node` VALUES
(112,'Nodo Padre','https://raw.githubusercontent.com/tuesta/treefx/refs/heads/Documentacion/assets/images/New%201.jpg','\0\0\0\0\0\0\0\0\0\0@‰Ûa@\0\0\0ÄÇÓY@'),
(113,'1','https://raw.githubusercontent.com/tuesta/treefx/refs/heads/Documentacion/assets/images/New%202.jpg','\0\0\0\0\0\0\0\0\0\0@ÄmK@\0\0\0†ú–v@'),
(114,'2','https://raw.githubusercontent.com/tuesta/treefx/refs/heads/Documentacion/assets/images/New%203.jpg','\0\0\0\0\0\0\0\0\0\0\0~Ïl@\0\0\0‡v@'),
(115,'3','https://raw.githubusercontent.com/tuesta/treefx/refs/heads/Documentacion/assets/images/New%205.jpg','\0\0\0\0\0\0\0\0\0\0\0¸T`@\0\0\0‡òcÇ@'),
(116,'4','https://raw.githubusercontent.com/tuesta/treefx/refs/heads/Documentacion/assets/images/New%204.jpg','\0\0\0\0\0\0\0\0\0\0@îau@\0\0\0 π3Ç@');
/*!40000 ALTER TABLE `node` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `node_hierarchy`
--

DROP TABLE IF EXISTS `node_hierarchy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `node_hierarchy` (
  `parent_node_id` int(11) NOT NULL,
  `child_node_id` int(11) NOT NULL,
  PRIMARY KEY (`parent_node_id`,`child_node_id`),
  UNIQUE KEY `unique_relation` (`parent_node_id`,`child_node_id`),
  KEY `child_node_id` (`child_node_id`),
  CONSTRAINT `node_hierarchy_ibfk_1` FOREIGN KEY (`parent_node_id`) REFERENCES `node` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `node_hierarchy_ibfk_2` FOREIGN KEY (`child_node_id`) REFERENCES `node` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `node_hierarchy`
--

LOCK TABLES `node_hierarchy` WRITE;
/*!40000 ALTER TABLE `node_hierarchy` DISABLE KEYS */;
INSERT INTO `node_hierarchy` VALUES
(112,113),
(112,114),
(114,115),
(114,116);
/*!40000 ALTER TABLE `node_hierarchy` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `node_positions`
--

DROP TABLE IF EXISTS `node_positions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `node_positions` (
  `position` point NOT NULL,
  `node_id` int(11) NOT NULL,
  `movements` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`position`(25)),
  KEY `node_id` (`node_id`),
  CONSTRAINT `node_positions_ibfk_1` FOREIGN KEY (`node_id`) REFERENCES `node` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `node_positions`
--

LOCK TABLES `node_positions` WRITE;
/*!40000 ALTER TABLE `node_positions` DISABLE KEYS */;
INSERT INTO `node_positions` VALUES
('\0\0\0\0\0\0\0Å9ûß£æ?ØH◊zÖÚ‡?',114,'Up Down(1) '),
('\0\0\0\0\0\0\0Å9ûß£Œ?kúu8\0€?',114,'Up '),
('\0\0\0\0\0\0\0ïYï`π÷?QëÍ\"•EË?',112,'Down(2) '),
('\0\0\0\0\0\0\0˛ˆâ&ûÁ?¢_è)¡Ê?',113,'Up Down(2) Down(2) '),
('\0\0\0\0\0\0\0##≠ìæòÃ?èLJºΩ8Ï?',112,'Down(2) Down(1) '),
('\0\0\0\0\0\0\09$.øgﬂ?PgO)ﬁ?',113,'Up '),
('\0\0\0\0\0\0\09$.øgﬂ?â·5ÏPw‰?',114,'Down(2) '),
('\0\0\0\0\0\0\09$.øgﬂ?¢_è)¡Ê?',113,'Up Down(2) Down(1) '),
('\0\0\0\0\0\0\0=BﬁtTà„?»∆0∏ıÜ‚?',113,'Up Down(2) '),
('\0\0\0\0\0\0\0^⁄¡Óérµ?QëÍ\"•EË?',112,'Down(1) '),
('\0\0\0\0\0\0\0sò‹‡a&œ?Õ[‡∫Ód‰?',114,'Down(1) '),
('\0\0\0\0\0\0\0ç*HΩ+‘?¨Q@≥ﬂ?',116,'Up Up Down(1) '),
('\0\0\0\0\0\0\0ç*HΩ+‘?è∑¸¢w*ﬂ?',115,'Up Up Down(1) '),
('\0\0\0\0\0\0\0ñ\nQ‹?QëÍ\"•Eÿ?',116,'Up Up '),
('\0\0\0\0\0\0\0ñ\nQ‹?Áná•„?',116,'Up Down(1) '),
('\0\0\0\0\0\0\0æ;—DyÍﬂ?èLJºΩ8Ï?',112,'Down(2) Down(2) '),
('\0\0\0\0\0\0\0ÀÒd1IıÂ?Áná•„?',115,'Up Down(2) '),
('\0\0\0\0\0\0\0œ∏/ß‘€?QëÍ\"•Eÿ?',115,'Up Up '),
('\0\0\0\0\0\0\0Ï5Lwﬂ·?è∑¸¢w*ﬂ?',115,'Up '),
('\0\0\0\0\0\0\0Ï5Lwﬂ·?¢†¶›Ó‡ﬁ?',116,'Up ');
/*!40000 ALTER TABLE `node_positions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roots`
--

DROP TABLE IF EXISTS `roots`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `roots` (
  `node_id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`node_id`),
  CONSTRAINT `roots_ibfk_1` FOREIGN KEY (`node_id`) REFERENCES `node` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roots`
--

LOCK TABLES `roots` WRITE;
/*!40000 ALTER TABLE `roots` DISABLE KEYS */;
INSERT INTO `roots` VALUES
(112,'Tutorial TreeFX');
/*!40000 ALTER TABLE `roots` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER AfterDeleteRoot
AFTER DELETE ON roots
FOR EACH ROW
BEGIN
    CALL DeleteRootAndChildren(OLD.node_id);
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Dumping routines for database 'treefx'
--
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
/*!50003 DROP PROCEDURE IF EXISTS `DeleteRootAndChildren` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `DeleteRootAndChildren`(IN root_id int)
BEGIN
    -- Crear tabla temporal para almacenar los node_id
    CREATE TEMPORARY TABLE IF NOT EXISTS temp_nodes (node_id INT PRIMARY KEY);
    DELETE FROM temp_nodes;

    -- CTE recursiva para obtener todos los nodos (ra√≠z + hijos)
    INSERT INTO temp_nodes
    WITH RECURSIVE AllNodes AS (
        SELECT root_id AS node_id -- Incluye el nodo ra√≠z
        UNION ALL
        SELECT nh.child_node_id
        FROM node_hierarchy nh
        INNER JOIN AllNodes an ON nh.parent_node_id = an.node_id
    )
    SELECT node_id FROM AllNodes;

    -- Eliminar datos en orden seguro (primero tablas dependientes, luego la tabla principal)
    DELETE FROM node_positions WHERE node_id IN (SELECT node_id FROM temp_nodes);
    DELETE FROM node_hierarchy
    WHERE parent_node_id IN (SELECT node_id FROM temp_nodes)
       OR child_node_id IN (SELECT node_id FROM temp_nodes);
    DELETE FROM node WHERE id IN (SELECT node_id FROM temp_nodes);

    DROP TEMPORARY TABLE temp_nodes;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
/*!50003 DROP PROCEDURE IF EXISTS `InsertChildNode` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `InsertChildNode`(IN node_name varchar(50), IN node_imgURL varchar(200),
                                                       IN node_position point, IN parent_id int, OUT new_node_id int)
    NO SQL
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    -- 1. Validaciones obligatorias
    IF parent_id IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Se requiere un ID de padre v√É¬°lido';
    END IF;

    IF NOT EXISTS (SELECT 1 FROM node WHERE id = parent_id) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El nodo padre no existe';
    END IF;

    -- 2. Insertar el nuevo nodo
    INSERT INTO node (`name`, `imgURL`, `position`)
    VALUES (node_name, node_imgURL, node_position);

    -- 3. Obtener ID del nuevo nodo
    SET @new_node_id = LAST_INSERT_ID();

    -- 4. Crear relaci√É¬≥n padre-hijo
    INSERT INTO node_hierarchy (`parent_node_id`, `child_node_id`)
    VALUES (parent_id, @new_node_id);

    SET new_node_id = @new_node_id;

    COMMIT;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION' */ ;
/*!50003 DROP PROCEDURE IF EXISTS `InsertRootNode` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `InsertRootNode`(IN node_name varchar(50), IN node_imgURL varchar(200),
                                                      IN node_position point, IN root_name varchar(100),
                                                      OUT new_root_id int)
    NO SQL
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    -- 1. Insertar el nodo principal
    INSERT INTO `node` (`name`, `imgURL`, `position`)
    VALUES (node_name, node_imgURL, node_position);

    -- 2. Obtener el ID del nuevo nodo
    SET @new_node_id = LAST_INSERT_ID();

    -- 3. Insertar en la tabla roots (asume que existe)
    INSERT INTO `roots` (`node_id`, `name`)
    VALUES (@new_node_id, root_name);

    -- 4. Retorna el ID generado
    SET new_root_id = @new_node_id;

    COMMIT;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-23 22:38:00
