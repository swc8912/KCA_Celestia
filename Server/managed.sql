CREATE DATABASE  IF NOT EXISTS `managed` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `managed`;
-- MySQL dump 10.13  Distrib 5.5.24, for osx10.5 (i386)
--
-- Host: 14.63.223.201    Database: managed
-- ------------------------------------------------------
-- Server version	5.5.27

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `qrtable`
--

DROP TABLE IF EXISTS `qrtable`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qrtable` (
  `idx` int(11) NOT NULL,
  `qrcode` varchar(45) CHARACTER SET latin1 NOT NULL,
  `regnum` varchar(45) NOT NULL,
  PRIMARY KEY (`qrcode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtable`
--

LOCK TABLES `qrtable` WRITE;
/*!40000 ALTER TABLE `qrtable` DISABLE KEYS */;
INSERT INTO `qrtable` VALUES (0,'http://m.site.naver.com/03S2Z','2'),(0,'{\"building\":\"AA\",\"room\":\"0000\",\"seat\":\"001\"}','202211111');
/*!40000 ALTER TABLE `qrtable` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `blacklist`
--

DROP TABLE IF EXISTS `blacklist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `blacklist` (
  `idx` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `package` varchar(45) NOT NULL,
  PRIMARY KEY (`idx`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `blacklist`
--

LOCK TABLES `blacklist` WRITE;
/*!40000 ALTER TABLE `blacklist` DISABLE KEYS */;
/*!40000 ALTER TABLE `blacklist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `loginuser`
--

DROP TABLE IF EXISTS `loginuser`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `loginuser` (
  `idx` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `regnum` varchar(45) NOT NULL,
  `passwd` varchar(45) NOT NULL,
  `infourl` varchar(45) NOT NULL,
  `perm` int(11) DEFAULT '0',
  PRIMARY KEY (`idx`,`regnum`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `loginuser`
--

LOCK TABLES `loginuser` WRITE;
/*!40000 ALTER TABLE `loginuser` DISABLE KEYS */;
INSERT INTO `loginuser` VALUES (1,'202211111','1234','http://www.naver.com/',0),(3,'200911399','asdf','http://www.naver.com/',0),(4,'1','1','http://www.naver.com/',0),(5,'2','2','http://www.naver.com/',0),(6,'3','3','http://www.naver.com/',0),(7,'4','4','http://www.naver.com/',0),(8,'5','5','http://www.naver.com/',0),(9,'6','6','http://www.naver.com/',0),(10,'7','7','http://www.naver.com/',0),(11,'8','8','http://www.naver.com/',0),(12,'9','9','http://www.naver.com/',0),(13,'root','123','',1);
/*!40000 ALTER TABLE `loginuser` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `timetable`
--

DROP TABLE IF EXISTS `timetable`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `timetable` (
  `idx` int(11) NOT NULL,
  `regnum` varchar(45) NOT NULL,
  `list` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`regnum`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `timetable`
--

LOCK TABLES `timetable` WRITE;
/*!40000 ALTER TABLE `timetable` DISABLE KEYS */;
/*!40000 ALTER TABLE `timetable` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lecturelist`
--

DROP TABLE IF EXISTS `lecturelist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lecturelist` (
  `idx` int(11) NOT NULL,
  `name` varchar(45) CHARACTER SET latin1 NOT NULL,
  `professor` varchar(45) CHARACTER SET latin1 NOT NULL,
  `code` int(11) NOT NULL,
  `starttime` varchar(45) CHARACTER SET latin1 DEFAULT NULL,
  `endtime` varchar(45) CHARACTER SET latin1 DEFAULT NULL,
  `infourl` varchar(100) DEFAULT NULL,
  `room` varchar(45) DEFAULT NULL,
  `building` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`code`),
  UNIQUE KEY `code_UNIQUE` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lecturelist`
--

LOCK TABLES `lecturelist` WRITE;
/*!40000 ALTER TABLE `lecturelist` DISABLE KEYS */;
INSERT INTO `lecturelist` VALUES (1,'?','?????',2222,'09:00','12:00','http://www.daum.net/','0000','AA');
/*!40000 ALTER TABLE `lecturelist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `whitelist`
--

DROP TABLE IF EXISTS `whitelist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `whitelist` (
  `idx` int(11) DEFAULT NULL,
  `name` varchar(45) DEFAULT NULL,
  `package` varchar(45) NOT NULL DEFAULT '',
  PRIMARY KEY (`package`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `whitelist`
--

LOCK TABLES `whitelist` WRITE;
/*!40000 ALTER TABLE `whitelist` DISABLE KEYS */;
INSERT INTO `whitelist` VALUES (1,'celes','celestia'),(2,'launcher','launcher'),(3,'setting','setting');
/*!40000 ALTER TABLE `whitelist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `serverip`
--

DROP TABLE IF EXISTS `serverip`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `serverip` (
  `idx` int(11) NOT NULL AUTO_INCREMENT,
  `ip` varchar(45) NOT NULL,
  PRIMARY KEY (`idx`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `serverip`
--

LOCK TABLES `serverip` WRITE;
/*!40000 ALTER TABLE `serverip` DISABLE KEYS */;
INSERT INTO `serverip` VALUES (1,'203.252.146'),(2,'183.109.102'),(3,'192.168.1');
/*!40000 ALTER TABLE `serverip` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2012-09-24  5:30:05
