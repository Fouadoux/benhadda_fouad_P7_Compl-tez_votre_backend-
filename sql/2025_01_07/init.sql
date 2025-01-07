-- MySQL dump 10.13  Distrib 8.0.38, for Win64 (x86_64)
--
-- Host: localhost    Database: demo
-- ------------------------------------------------------
-- Server version	8.0.39

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `bidlist`
--

DROP TABLE IF EXISTS `bidlist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bidlist` (
  `bid_list_id` tinyint NOT NULL AUTO_INCREMENT,
  `account` varchar(30) NOT NULL,
  `ask` double DEFAULT NULL,
  `ask_quantity` double DEFAULT NULL,
  `benchmark` varchar(125) DEFAULT NULL,
  `bid` double DEFAULT NULL,
  `bid_list_date` datetime(6) DEFAULT NULL,
  `bid_quantity` double DEFAULT NULL,
  `book` varchar(125) DEFAULT NULL,
  `commentary` varchar(125) DEFAULT NULL,
  `creation_date` datetime(6) DEFAULT NULL,
  `creation_name` varchar(125) DEFAULT NULL,
  `deal_name` varchar(125) DEFAULT NULL,
  `deal_type` varchar(125) DEFAULT NULL,
  `revision_date` datetime(6) DEFAULT NULL,
  `revision_name` varchar(125) DEFAULT NULL,
  `security` varchar(125) DEFAULT NULL,
  `side` varchar(125) DEFAULT NULL,
  `source_list_id` varchar(125) DEFAULT NULL,
  `status` varchar(10) DEFAULT NULL,
  `trader` varchar(125) DEFAULT NULL,
  `type` varchar(30) NOT NULL,
  PRIMARY KEY (`bid_list_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `curvepoint`
--

DROP TABLE IF EXISTS `curvepoint`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `curvepoint` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `as_of_date` datetime(6) DEFAULT NULL,
  `creation_date` datetime(6) DEFAULT NULL,
  `curve_id` int NOT NULL,
  `term` double NOT NULL,
  `value` double NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK3199atmgukekp0fcl5s2l66vx` (`curve_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rating`
--

DROP TABLE IF EXISTS `rating`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rating` (
  `id` int NOT NULL AUTO_INCREMENT,
  `sandprating` varchar(125) NOT NULL,
  `fitch_rating` varchar(125) NOT NULL,
  `moodys_rating` varchar(125) NOT NULL,
  `order_number` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `rulename`
--

DROP TABLE IF EXISTS `rulename`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rulename` (
  `id` int NOT NULL AUTO_INCREMENT,
  `description` varchar(125) NOT NULL,
  `json` varchar(125) NOT NULL,
  `name` varchar(125) NOT NULL,
  `sql_part` varchar(125) NOT NULL,
  `sql_str` varchar(125) NOT NULL,
  `template` varchar(512) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `trade`
--

DROP TABLE IF EXISTS `trade`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `trade` (
  `trade_id` int NOT NULL AUTO_INCREMENT,
  `account` varchar(30) NOT NULL,
  `benchmark` varchar(125) DEFAULT NULL,
  `book` varchar(125) DEFAULT NULL,
  `buy_price` double DEFAULT NULL,
  `buy_quantity` double DEFAULT NULL,
  `creation_date` datetime(6) DEFAULT NULL,
  `creation_name` varchar(125) DEFAULT NULL,
  `deal_name` varchar(125) DEFAULT NULL,
  `deal_type` varchar(125) DEFAULT NULL,
  `revision_date` datetime(6) DEFAULT NULL,
  `revision_name` varchar(125) DEFAULT NULL,
  `security` varchar(125) DEFAULT NULL,
  `sell_price` double DEFAULT NULL,
  `sell_quantity` double DEFAULT NULL,
  `side` varchar(125) DEFAULT NULL,
  `source_list_id` varchar(125) DEFAULT NULL,
  `status` varchar(10) DEFAULT NULL,
  `trade_date` datetime(6) DEFAULT NULL,
  `trader` varchar(125) DEFAULT NULL,
  `type` varchar(30) NOT NULL,
  PRIMARY KEY (`trade_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `fullname` varchar(125) NOT NULL,
  `password` varchar(125) DEFAULT NULL,
  `role` varchar(125) NOT NULL,
  `username` varchar(125) NOT NULL,
  `github_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Insertion d'un utilisateur par défaut
--

INSERT INTO `users` (`fullname`, `password`, `role`, `username`, `github_id`) 
VALUES 
('Admin', '$2a$10$Vr9cSWyL/2P0WgbqDuejXuVgfIPNtqrxps3mBM9biDUsGXsw0bXJS', 'ROLE_ADMIN', 'admin', NULL);

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-01-07 11:59:49
