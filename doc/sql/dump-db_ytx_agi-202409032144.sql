-- MySQL dump 10.13  Distrib 8.0.19, for Win64 (x86_64)
--
-- Host: 192.168.31.125    Database: db_ytx_agent
-- ------------------------------------------------------
-- Server version	8.0.32



--
-- Table structure for table `ai_agent_property`
--

DROP TABLE IF EXISTS `ai_agent_property`;
CREATE TABLE `ai_agent_property` (
  `id` int NOT NULL AUTO_INCREMENT,
  `agent_id` int NOT NULL,
  `property_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `property_value` text COLLATE utf8mb4_unicode_ci,
  `property_group` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `ai_agent_property_agent_id_IDX` (`agent_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


--
-- Table structure for table `ai_agent_ref_intent`
--

DROP TABLE IF EXISTS `ai_agent_ref_intent`;
CREATE TABLE `ai_agent_ref_intent` (
  `id` int NOT NULL AUTO_INCREMENT,
  `agent_id` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `intent_id` int NOT NULL,
  `tenant_id` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `ai_agent_ref_intent_agent_id_IDX` (`agent_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



--
-- Table structure for table `ai_agent_ref_tool`
--

DROP TABLE IF EXISTS `ai_agent_ref_tool`;
CREATE TABLE `ai_agent_ref_tool` (
  `id` int NOT NULL AUTO_INCREMENT,
  `agent_id` int NOT NULL,
  `tool_id` int NOT NULL,
  `tenant_id` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `ai_agent_ref_tool_agent_id_IDX` (`agent_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



--
-- Table structure for table `ai_agent`
--

DROP TABLE IF EXISTS `ai_agent`;
CREATE TABLE `ai_agent` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `code` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `version` int NOT NULL,
  `tenant_id` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `ai_agent_code_IDX` (`code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



--
-- Table structure for table `ai_intention`
--

DROP TABLE IF EXISTS `ai_intention`;
CREATE TABLE `ai_intention` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `desc` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `instruction` text COLLATE utf8mb4_unicode_ci,
  `sampleTasks` text COLLATE utf8mb4_unicode_ci,
  `intentRecognition` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tenant_id` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



--
-- Table structure for table `ai_skill`
--

DROP TABLE IF EXISTS `ai_skill`;
CREATE TABLE `ai_skill` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



--
-- Dumping routines for database 'db_ytx_agent'
--

-- Dump completed on 2024-09-03 21:44:37
