-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 18, 2024 at 03:14 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `monitoring`
--

-- --------------------------------------------------------

--
-- Table structure for table `tb_konveksi`
--

CREATE TABLE `tb_konveksi` (
  `id_konveksi` int(11) NOT NULL,
  `nama_produk` varchar(255) NOT NULL,
  `jenis` varchar(255) NOT NULL,
  `ukuran_s` int(11) NOT NULL,
  `ukuran_m` int(11) NOT NULL,
  `ukuran_l` int(11) NOT NULL,
  `ukuran_xl` int(11) NOT NULL,
  `ukuran_xxl` int(11) NOT NULL,
  `ukuran_xxxl` int(11) NOT NULL,
  `ukuran_5xl` int(11) NOT NULL,
  `ukuran_tambahan` int(11) NOT NULL,
  `informasi_tambahan` varchar(255) NOT NULL,
  `jumlah` varchar(255) NOT NULL,
  `tanggal` datetime DEFAULT NULL,
  `tgl_selesai` datetime DEFAULT NULL,
  `tanggal_pengambilan` date DEFAULT NULL,
  `gambar` text DEFAULT NULL,
  `status` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tb_konveksi`
--

INSERT INTO `tb_konveksi` (`id_konveksi`, `nama_produk`, `jenis`, `ukuran_s`, `ukuran_m`, `ukuran_l`, `ukuran_xl`, `ukuran_xxl`, `ukuran_xxxl`, `ukuran_5xl`, `ukuran_tambahan`, `informasi_tambahan`, `jumlah`, `tanggal`, `tgl_selesai`, `tanggal_pengambilan`, `gambar`, `status`) VALUES
(18, 'Kaos oversize ', 'Kaos ', 50, 50, 60, 10, 10, 10, 10, 0, '', '200', '2024-02-10 11:15:43', '2024-02-15 08:08:07', '2024-02-15', '1000119770_3479.jpg', 'Selesai'),
(19, 'Rugby lengan panjang ', 'kaos lengan panjang ', 40, 20, 50, 56, 20, 20, 20, 0, '', '226', '2024-02-10 11:17:34', '2024-02-10 16:14:37', '2024-02-26', '1000119810_9083.jpg', 'Selesai'),
(20, 'kaos oversize ', 'kaos ', 50, 20, 50, 20, 40, 20, 20, 0, '', '220', '2024-02-10 11:19:06', '2024-02-15 08:07:45', '2024-02-28', '1000119749_5910.jpg', 'Selesai'),
(21, 'Kaos oversize Rugby lengan pendek ', 'kaos', 20, 30, 40, 20, 20, 20, 30, 10, 'L size diameter 200cm', '190', '2024-02-10 11:20:36', NULL, '2024-03-02', '1000119822_1709.jpg', 'Diajukan'),
(22, 'celana cargo ', 'celana ', 8, 8, 5, 5, 3, 5, 5, 0, '', '39', '2024-02-14 11:49:51', NULL, '2024-02-15', '1000120007_1663.jpg', 'Diajukan');

-- --------------------------------------------------------

--
-- Table structure for table `tb_manajer`
--

CREATE TABLE `tb_manajer` (
  `id_manajer` int(11) NOT NULL,
  `nama` varchar(255) NOT NULL,
  `nohp` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tb_manajer`
--

INSERT INTO `tb_manajer` (`id_manajer`, `nama`, `nohp`) VALUES
(3, 'Manajer1', '082154697'),
(13, 'jageyggw22', '08455454111');

-- --------------------------------------------------------

--
-- Table structure for table `tb_monitoring`
--

CREATE TABLE `tb_monitoring` (
  `id` int(11) NOT NULL,
  `id_monitoring` int(11) NOT NULL,
  `id_pengerjaan` int(11) NOT NULL,
  `deskripsi` varchar(255) DEFAULT NULL,
  `gambar_proses` text DEFAULT NULL,
  `tanggal_selesai` datetime DEFAULT NULL,
  `status_pengerjaan` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tb_monitoring`
--

INSERT INTO `tb_monitoring` (`id`, `id_monitoring`, `id_pengerjaan`, `deskripsi`, `gambar_proses`, `tanggal_selesai`, `status_pengerjaan`) VALUES
(66, 18, 1, 'Masuk Tahap Selanjutnya', '', '2024-02-10 11:22:12', 'Selesai'),
(67, 18, 2, 'Masuk Tahap Selanjutnya', '', '2024-02-10 11:22:18', 'Selesai'),
(68, 18, 4, 'Masuk Tahap Selanjutnya', '', '2024-02-13 15:22:25', 'Selesai'),
(69, 18, 5, 'Masuk Tahap Selanjutnya', '', '2024-02-14 22:29:42', 'Selesai'),
(70, 18, 6, 'Masuk Tahap Selanjutnya', '', '1900-01-15 16:19:16', 'Selesai'),
(71, 18, 7, 'Masuk Tahap Selanjutnya', '', '2024-02-15 08:07:54', 'Selesai'),
(72, 18, 8, 'Masuk Tahap Selanjutnya', '', '2024-02-15 08:08:07', 'Selesai'),
(73, 19, 1, 'Masuk Tahap Selanjutnya', '1000122158_676.jpg', '2024-02-10 11:33:32', 'Selesai'),
(74, 19, 2, 'Masuk Tahap Selanjutnya', '1000119770_2212.jpg', '2024-02-10 15:58:17', 'Selesai'),
(79, 19, 4, 'Masuk Tahap Selanjutnya', '', '2024-02-10 16:13:47', 'Selesai'),
(82, 19, 5, 'Masuk Tahap Selanjutnya', '', '2024-02-10 16:14:06', 'Selesai'),
(83, 19, 6, 'Masuk Tahap Selanjutnya', '', '2024-02-10 16:14:20', 'Selesai'),
(84, 19, 7, 'Masuk Tahap Selanjutnya', '', '2024-02-10 16:14:28', 'Selesai'),
(85, 19, 8, 'Masuk Tahap Selanjutnya', '', '2024-02-10 16:24:55', 'Dikerjakan'),
(86, 20, 1, 'Masuk Tahap Selanjutnya', '', '2024-02-15 08:06:51', 'Selesai'),
(87, 20, 2, 'Masuk Tahap Selanjutnya', '', '2024-02-15 08:06:59', 'Selesai'),
(88, 20, 4, 'Masuk Tahap Selanjutnya', '', '2024-02-15 08:07:06', 'Selesai'),
(89, 20, 5, 'Masuk Tahap Selanjutnya', '', '2024-02-15 08:07:16', 'Selesai'),
(90, 20, 6, 'Masuk Tahap Selanjutnya', '', '2024-02-15 08:07:25', 'Selesai'),
(91, 20, 7, 'Masuk Tahap Selanjutnya', '', '2024-02-15 08:07:36', 'Selesai'),
(92, 20, 8, 'Masuk Tahap Selanjutnya', '', '2024-02-15 08:07:45', 'Selesai'),
(93, 18, 8, 'Masuk Tahap Selanjutnya', '', '2024-02-15 08:08:07', 'Selesai');

-- --------------------------------------------------------

--
-- Table structure for table `tb_pengawas`
--

CREATE TABLE `tb_pengawas` (
  `id_pengawas` int(11) NOT NULL,
  `nama` varchar(255) NOT NULL,
  `nohp` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tb_pengawas`
--

INSERT INTO `tb_pengawas` (`id_pengawas`, `nama`, `nohp`) VALUES
(4, 'pengawas1', '081254894'),
(12, 'sejege1', '0854257'),
(14, 'pengawas2', '085263570657');

-- --------------------------------------------------------

--
-- Table structure for table `tb_tahapmonitoring`
--

CREATE TABLE `tb_tahapmonitoring` (
  `id_tahap` int(11) NOT NULL,
  `nama_tahap` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tb_tahapmonitoring`
--

INSERT INTO `tb_tahapmonitoring` (`id_tahap`, `nama_tahap`) VALUES
(1, 'Desain'),
(2, 'Fit Cutting'),
(4, 'Sablon'),
(5, 'Menjahit'),
(6, 'Finishing'),
(7, 'Merk'),
(8, 'Packaging');

-- --------------------------------------------------------

--
-- Table structure for table `tb_user`
--

CREATE TABLE `tb_user` (
  `id_user` int(11) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` varchar(255) NOT NULL,
  `gambar` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tb_user`
--

INSERT INTO `tb_user` (`id_user`, `username`, `password`, `role`, `gambar`) VALUES
(1, 'admin123', 'admin123', 'Admin', '1000122157_2787.jpg'),
(3, 'manajer123', 'manajer123', 'Manajer', NULL),
(4, 'pengawas123', 'pengawas123', 'Pengawas', NULL),
(14, 'pengawas2', 'pengawas2', 'Pengawas', NULL);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `tb_konveksi`
--
ALTER TABLE `tb_konveksi`
  ADD PRIMARY KEY (`id_konveksi`);

--
-- Indexes for table `tb_manajer`
--
ALTER TABLE `tb_manajer`
  ADD PRIMARY KEY (`id_manajer`);

--
-- Indexes for table `tb_monitoring`
--
ALTER TABLE `tb_monitoring`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `tb_pengawas`
--
ALTER TABLE `tb_pengawas`
  ADD PRIMARY KEY (`id_pengawas`);

--
-- Indexes for table `tb_tahapmonitoring`
--
ALTER TABLE `tb_tahapmonitoring`
  ADD PRIMARY KEY (`id_tahap`);

--
-- Indexes for table `tb_user`
--
ALTER TABLE `tb_user`
  ADD PRIMARY KEY (`id_user`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `tb_konveksi`
--
ALTER TABLE `tb_konveksi`
  MODIFY `id_konveksi` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- AUTO_INCREMENT for table `tb_monitoring`
--
ALTER TABLE `tb_monitoring`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=94;

--
-- AUTO_INCREMENT for table `tb_tahapmonitoring`
--
ALTER TABLE `tb_tahapmonitoring`
  MODIFY `id_tahap` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `tb_user`
--
ALTER TABLE `tb_user`
  MODIFY `id_user` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
