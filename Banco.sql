-- phpMyAdmin SQL Dump
-- version 4.7.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: 21-Nov-2017 às 19:02
-- Versão do servidor: 10.1.25-MariaDB
-- PHP Version: 5.6.31

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `fixit`
--
CREATE DATABASE IF NOT EXISTS `fixit` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `fixit`;

-- --------------------------------------------------------

--
-- Estrutura da tabela `chamados`
--

CREATE TABLE `chamados` (
  `ID_Chamado` smallint(5) UNSIGNED NOT NULL,
  `ID_Usuario` tinyint(3) UNSIGNED NOT NULL,
  `ID_Usuario_Administrador` tinyint(3) UNSIGNED DEFAULT NULL,
  `Data_Inicial` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `Data_Final` datetime DEFAULT NULL,
  `ID_Computador` smallint(4) UNSIGNED NOT NULL,
  `ID_Problema` tinyint(1) UNSIGNED NOT NULL,
  `Observacao` varchar(255) DEFAULT NULL,
  `Status` tinyint(1) UNSIGNED NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Extraindo dados da tabela `chamados`
--

INSERT INTO `chamados` (`ID_Chamado`, `ID_Usuario`, `ID_Usuario_Administrador`, `Data_Inicial`, `Data_Final`, `ID_Computador`, `ID_Problema`, `Observacao`, `Status`) VALUES
(150, 7, NULL, '2017-11-21 15:56:54', NULL, 133, 25, 'PC explodiu', 1),
(151, 7, NULL, '2017-11-21 15:57:05', NULL, 204, 22, '', 1),
(152, 7, NULL, '2017-11-21 15:57:13', NULL, 174, 23, 'quebrou', 1),
(153, 7, NULL, '2017-11-21 15:57:30', NULL, 198, 29, 'windows bugou', 1),
(154, 7, NULL, '2017-11-21 15:57:50', NULL, 179, 27, 'não abre', 1);

-- --------------------------------------------------------

--
-- Estrutura da tabela `computadores`
--

CREATE TABLE `computadores` (
  `ID_Computador` smallint(4) UNSIGNED NOT NULL,
  `Numero` tinyint(2) UNSIGNED NOT NULL,
  `ID_Sala` smallint(5) UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Extraindo dados da tabela `computadores`
--

INSERT INTO `computadores` (`ID_Computador`, `Numero`, `ID_Sala`) VALUES
(133, 1, 101),
(163, 1, 202),
(193, 1, 303),
(134, 2, 101),
(164, 2, 202),
(194, 2, 303),
(135, 3, 101),
(165, 3, 202),
(195, 3, 303),
(136, 4, 101),
(166, 4, 202),
(196, 4, 303),
(137, 5, 101),
(167, 5, 202),
(197, 5, 303),
(138, 6, 101),
(168, 6, 202),
(198, 6, 303),
(139, 7, 101),
(169, 7, 202),
(199, 7, 303),
(140, 8, 101),
(170, 8, 202),
(200, 8, 303),
(141, 9, 101),
(171, 9, 202),
(201, 9, 303),
(142, 10, 101),
(172, 10, 202),
(202, 10, 303),
(143, 11, 101),
(173, 11, 202),
(203, 11, 303),
(144, 12, 101),
(174, 12, 202),
(204, 12, 303),
(145, 13, 101),
(175, 13, 202),
(205, 13, 303),
(146, 14, 101),
(176, 14, 202),
(206, 14, 303),
(147, 15, 101),
(177, 15, 202),
(207, 15, 303),
(148, 16, 101),
(178, 16, 202),
(208, 16, 303),
(149, 17, 101),
(179, 17, 202),
(209, 17, 303),
(150, 18, 101),
(180, 18, 202),
(210, 18, 303),
(151, 19, 101),
(181, 19, 202),
(211, 19, 303),
(152, 20, 101),
(182, 20, 202),
(212, 20, 303),
(153, 21, 101),
(183, 21, 202),
(213, 21, 303),
(154, 22, 101),
(184, 22, 202),
(214, 22, 303),
(155, 23, 101),
(185, 23, 202),
(215, 23, 303),
(156, 24, 101),
(186, 24, 202),
(216, 24, 303),
(157, 25, 101),
(187, 25, 202),
(217, 25, 303),
(158, 26, 101),
(188, 26, 202),
(218, 26, 303),
(159, 27, 101),
(189, 27, 202),
(219, 27, 303),
(160, 28, 101),
(190, 28, 202),
(220, 28, 303),
(161, 29, 101),
(191, 29, 202),
(221, 29, 303),
(162, 30, 101),
(192, 30, 202),
(222, 30, 303);

-- --------------------------------------------------------

--
-- Estrutura da tabela `problemas`
--

CREATE TABLE `problemas` (
  `ID_Problema` tinyint(3) UNSIGNED NOT NULL,
  `Descricao` varchar(255) NOT NULL,
  `Tipo` tinyint(1) UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Extraindo dados da tabela `problemas`
--

INSERT INTO `problemas` (`ID_Problema`, `Descricao`, `Tipo`) VALUES
(24, 'Monitor', 1),
(22, 'Mouse', 1),
(25, 'Outros', 1),
(23, 'Teclado', 1),
(26, 'Android Studio', 2),
(27, 'Netbeans', 2),
(29, 'Outros', 2),
(28, 'Xampp', 2);

-- --------------------------------------------------------

--
-- Estrutura da tabela `salas`
--

CREATE TABLE `salas` (
  `ID_Sala` smallint(5) UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Extraindo dados da tabela `salas`
--

INSERT INTO `salas` (`ID_Sala`) VALUES
(101),
(202),
(303);

-- --------------------------------------------------------

--
-- Estrutura da tabela `usuarios`
--

CREATE TABLE `usuarios` (
  `ID_Usuario` tinyint(3) UNSIGNED NOT NULL,
  `Nome` varchar(45) NOT NULL,
  `Email` varchar(140) NOT NULL,
  `Senha` varchar(20) NOT NULL,
  `isAdministrador` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Extraindo dados da tabela `usuarios`
--

INSERT INTO `usuarios` (`ID_Usuario`, `Nome`, `Email`, `Senha`, `isAdministrador`) VALUES
(1, 'a', 'a@a.a', 'OM#90o', 1),
(2, 'Joao', 'joaophickmann@outlook.com', '0rJ-DbXrcj@xm-dS#', 0),
(3, 'Guilherme', 'guikipper1@gmail.com', '+@Yw9L', 0);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `chamados`
--
ALTER TABLE `chamados`
  ADD PRIMARY KEY (`ID_Chamado`),
  ADD KEY `fk_Chamadas_Usuarios1_idx` (`ID_Usuario`),
  ADD KEY `fk_Chamadas_Usuarios2_idx` (`ID_Usuario_Administrador`),
  ADD KEY `fk_Chamadas_Computadores1_idx` (`ID_Computador`),
  ADD KEY `fk_Chamadas_Problemas1_idx` (`ID_Problema`);

--
-- Indexes for table `computadores`
--
ALTER TABLE `computadores`
  ADD PRIMARY KEY (`ID_Computador`),
  ADD UNIQUE KEY `computador_UNIQUE` (`Numero`,`ID_Sala`),
  ADD KEY `fk_Computadores_Salas_idx` (`ID_Sala`);

--
-- Indexes for table `problemas`
--
ALTER TABLE `problemas`
  ADD PRIMARY KEY (`ID_Problema`),
  ADD UNIQUE KEY `problema_UNIQUE` (`Tipo`,`Descricao`);

--
-- Indexes for table `salas`
--
ALTER TABLE `salas`
  ADD PRIMARY KEY (`ID_Sala`);

--
-- Indexes for table `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`ID_Usuario`),
  ADD UNIQUE KEY `Email_UNIQUE` (`Email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `chamados`
--
ALTER TABLE `chamados`
  MODIFY `ID_Chamado` smallint(5) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=155;
--
-- AUTO_INCREMENT for table `computadores`
--
ALTER TABLE `computadores`
  MODIFY `ID_Computador` smallint(4) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=223;
--
-- AUTO_INCREMENT for table `problemas`
--
ALTER TABLE `problemas`
  MODIFY `ID_Problema` tinyint(3) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=30;
--
-- AUTO_INCREMENT for table `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `ID_Usuario` tinyint(3) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;
--
-- Constraints for dumped tables
--

--
-- Limitadores para a tabela `chamados`
--
ALTER TABLE `chamados`
  ADD CONSTRAINT `fk_Chamadas_Computadores1` FOREIGN KEY (`ID_Computador`) REFERENCES `computadores` (`ID_Computador`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_Chamadas_Problemas1` FOREIGN KEY (`ID_Problema`) REFERENCES `problemas` (`ID_Problema`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_Chamadas_Usuarios1` FOREIGN KEY (`ID_Usuario`) REFERENCES `usuarios` (`ID_Usuario`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_Chamadas_Usuarios2` FOREIGN KEY (`ID_Usuario_Administrador`) REFERENCES `usuarios` (`ID_Usuario`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Limitadores para a tabela `computadores`
--
ALTER TABLE `computadores`
  ADD CONSTRAINT `fk_Computadores_Salas` FOREIGN KEY (`ID_Sala`) REFERENCES `salas` (`ID_Sala`) ON DELETE NO ACTION ON UPDATE NO ACTION;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
