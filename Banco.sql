-- phpMyAdmin SQL Dump
-- version 4.7.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: 20-Nov-2017 às 03:19
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
-- Database: `trabalhofinal`
--

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
(78, 5, NULL, '2017-11-18 19:19:37', NULL, 1, 6, '', 1),
(79, 5, 4, '2017-11-18 19:19:40', NULL, 1, 6, '', 2),
(82, 4, 4, '2017-11-18 21:12:42', '2017-11-18 22:37:11', 1, 6, '', 3),
(88, 4, 4, '2017-11-18 23:23:04', '2017-11-19 13:11:57', 132, 2, '', 3),
(109, 4, 4, '2017-11-19 16:09:07', NULL, 103, 6, '', 2),
(124, 4, NULL, '2017-11-20 00:18:17', NULL, 103, 6, '', 1);

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
(103, 1, 100),
(1, 1, 101),
(2, 1, 202),
(3, 1, 303),
(104, 2, 100),
(105, 3, 100),
(106, 4, 100),
(107, 5, 100),
(108, 6, 100),
(109, 7, 100),
(110, 8, 100),
(111, 9, 100),
(112, 10, 100),
(113, 11, 100),
(114, 12, 100),
(115, 13, 100),
(116, 14, 100),
(117, 15, 100),
(118, 16, 100),
(119, 17, 100),
(120, 18, 100),
(121, 19, 100),
(122, 20, 100),
(123, 21, 100),
(124, 22, 100),
(125, 23, 100),
(126, 24, 100),
(127, 25, 100),
(128, 26, 100),
(129, 27, 100),
(130, 28, 100),
(131, 29, 100),
(132, 30, 100);

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
(6, 'Mouse', 1),
(18, 'Outros', 1),
(2, 'Netbeans', 2),
(19, 'Outros', 2);

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
(100),
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
(1, 'João', 'joaophickmann@outlook.com', '123456', 0),
(2, 'Gordo da TI', 'bacon@carne.com', 'coxinha', 0),
(3, 'Guilherme', 'guikipper1@gmail.com', 'sougay', 0),
(4, 'foda', 'a@a.a', 'OM#90o', 1),
(5, 'joao', 'ze@bol.a', 'P0yFN277', 0),
(6, 'fernando', 'gui@gay.boiologo', '+@Yw9L', 0);

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
  MODIFY `ID_Chamado` smallint(5) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=125;
--
-- AUTO_INCREMENT for table `computadores`
--
ALTER TABLE `computadores`
  MODIFY `ID_Computador` smallint(4) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=133;
--
-- AUTO_INCREMENT for table `problemas`
--
ALTER TABLE `problemas`
  MODIFY `ID_Problema` tinyint(3) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;
--
-- AUTO_INCREMENT for table `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `ID_Usuario` tinyint(3) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;
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