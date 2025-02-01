-- phpMyAdmin SQL Dump
-- version 4.9.7
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Oct 30, 2022 at 10:44 PM
-- Server version: 5.7.23-23
-- PHP Version: 7.4.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `vivoraj1_onboarding`
--

-- --------------------------------------------------------

--
-- Table structure for table `add_users`
--

CREATE TABLE `add_users` (
  `id` int(11) NOT NULL,
  `userid` varchar(250) NOT NULL,
  `password` varchar(250) NOT NULL,
  `joining_date` varchar(255) NOT NULL,
  `designation` varchar(250) NOT NULL,
  `department` varchar(250) NOT NULL,
  `grade` varchar(250) NOT NULL,
  `branch` varchar(250) NOT NULL,
  `zone` varchar(250) NOT NULL,
  `employee_level` varchar(255) NOT NULL,
  `category` varchar(250) NOT NULL,
  `user_status` varchar(255) NOT NULL,
  `salary` varchar(255) NOT NULL,
  `create_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `candidate_category` varchar(255) NOT NULL,
  `app_version` varchar(255) NOT NULL,
  `image` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `branch_table`
--

CREATE TABLE `branch_table` (
  `id` int(11) NOT NULL,
  `branch` varchar(255) NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `check_list_table`
--

CREATE TABLE `check_list_table` (
  `id` int(11) NOT NULL,
  `userid` varchar(255) NOT NULL,
  `employee_level` varchar(255) NOT NULL,
  `job_application` varchar(255) NOT NULL,
  `interview_assessment` varchar(255) NOT NULL,
  `updated_signed_cv` varchar(255) NOT NULL,
  `joining_form` varchar(255) NOT NULL,
  `company_offer_letter` varchar(255) NOT NULL,
  `company_apmt_letter_copy` varchar(255) NOT NULL,
  `company_fixed_apmt_letter_copy` varchar(255) NOT NULL,
  `education_certificate` varchar(255) NOT NULL,
  `last_exp_letter` varchar(255) NOT NULL,
  `pan_card_copy` varchar(255) NOT NULL,
  `aadhar_card_copy` varchar(255) NOT NULL,
  `passport_copy` varchar(255) NOT NULL,
  `voter_id_copy` varchar(255) NOT NULL,
  `salary_slip` varchar(255) NOT NULL,
  `bank_stmt` varchar(255) NOT NULL,
  `appointment_letter` varchar(255) NOT NULL,
  `form_11_pf` varchar(255) NOT NULL,
  `form_1_esic` varchar(255) NOT NULL,
  `form_g` varchar(255) NOT NULL,
  `employee_cfdtl_above` varchar(255) NOT NULL,
  `employee_cfdtl_below` varchar(255) NOT NULL,
  `bank_details` varchar(255) NOT NULL,
  `v_work_aggrement` varchar(255) NOT NULL,
  `hiring_approval` varchar(255) NOT NULL,
  `covid_vaccine_certificate` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `department_table`
--

CREATE TABLE `department_table` (
  `id` int(11) NOT NULL,
  `department` varchar(255) NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `designation_table`
--

CREATE TABLE `designation_table` (
  `id` int(11) NOT NULL,
  `designation` varchar(255) NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `employee_experience_table`
--

CREATE TABLE `employee_experience_table` (
  `id` int(11) NOT NULL,
  `userid` varchar(255) NOT NULL,
  `company_name` varchar(255) NOT NULL,
  `company_job_title` varchar(255) NOT NULL,
  `company_start_date` varchar(255) NOT NULL,
  `company_end_date` varchar(255) NOT NULL,
  `experience_letter` varchar(255) NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `employee_insurance_table`
--

CREATE TABLE `employee_insurance_table` (
  `id` int(11) NOT NULL,
  `userid` varchar(255) CHARACTER SET latin1 NOT NULL,
  `member_relation` varchar(255) CHARACTER SET latin1 NOT NULL,
  `member_name` varchar(255) CHARACTER SET latin1 NOT NULL,
  `member_dob` varchar(255) CHARACTER SET latin1 NOT NULL,
  `member_ac_front_image` varchar(255) CHARACTER SET latin1 NOT NULL,
  `member_ac_back_image` varchar(255) CHARACTER SET latin1 NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `employee_level_table`
--

CREATE TABLE `employee_level_table` (
  `id` int(11) NOT NULL,
  `employee_level` varchar(255) NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `employee_notification_table`
--

CREATE TABLE `employee_notification_table` (
  `id` int(11) NOT NULL,
  `userid` varchar(255) NOT NULL,
  `notification_title` varchar(255) NOT NULL,
  `notification_text` varchar(255) NOT NULL,
  `notification_status` varchar(255) NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `employee_personal_detail`
--

CREATE TABLE `employee_personal_detail` (
  `id` int(11) NOT NULL,
  `userid` varchar(255) NOT NULL,
  `initial` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `father_name` varchar(255) NOT NULL,
  `mother_name` varchar(255) NOT NULL,
  `joining_date` varchar(255) NOT NULL,
  `dob` varchar(255) NOT NULL,
  `gender` varchar(255) NOT NULL,
  `blood_group` varchar(255) NOT NULL,
  `marital_status` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `number` varchar(255) NOT NULL,
  `pin_code` varchar(255) NOT NULL,
  `aadhar_no` varchar(255) NOT NULL,
  `pan_no` varchar(255) NOT NULL,
  `uan_no` varchar(255) NOT NULL,
  `esic_no` varchar(255) NOT NULL,
  `permanent_address` varchar(255) NOT NULL,
  `current_address` varchar(255) NOT NULL,
  `work_with_company` varchar(255) NOT NULL,
  `work_with_company_department` varchar(255) NOT NULL,
  `work_with_company_designation` varchar(255) NOT NULL,
  `work_with_company_doj` varchar(255) NOT NULL,
  `work_with_company_dol` varchar(255) NOT NULL,
  `company_relation` varchar(255) NOT NULL,
  `company_relation_name` varchar(255) NOT NULL,
  `company_relation_relation` varchar(255) NOT NULL,
  `company_relation_designation` varchar(255) NOT NULL,
  `10_school` varchar(255) NOT NULL,
  `10_board` varchar(255) NOT NULL,
  `10_percentage` varchar(255) NOT NULL,
  `10_year_passing` varchar(255) NOT NULL,
  `10_image` varchar(255) NOT NULL,
  `12_choice` varchar(255) NOT NULL,
  `12_school` varchar(255) NOT NULL,
  `12_board` varchar(255) NOT NULL,
  `12_percentage` varchar(255) NOT NULL,
  `12_year_passing` varchar(255) NOT NULL,
  `12_image` varchar(255) NOT NULL,
  `graduation_choice` varchar(255) NOT NULL,
  `graduation_course` varchar(255) NOT NULL,
  `graduation_university` varchar(255) NOT NULL,
  `graduation_board` varchar(255) NOT NULL,
  `graduation_percentage` varchar(255) NOT NULL,
  `graduation_year_passing` varchar(255) NOT NULL,
  `graduation_image` varchar(255) NOT NULL,
  `post_graduation_choice` varchar(255) NOT NULL,
  `post_graduation_course` varchar(255) NOT NULL,
  `post_graduation_university` varchar(255) NOT NULL,
  `post_graduation_board` varchar(255) NOT NULL,
  `post_graduation_percentage` varchar(255) NOT NULL,
  `post_graduation_year_passing` varchar(255) NOT NULL,
  `post_graduation_image` varchar(255) NOT NULL,
  `other_qualification_choice` varchar(255) NOT NULL,
  `other_qualification_course` varchar(255) NOT NULL,
  `other_qualification_university` varchar(255) NOT NULL,
  `other_qualification_board` varchar(255) NOT NULL,
  `other_qualification_percentage` varchar(255) NOT NULL,
  `other_qualification_year_passing` varchar(255) NOT NULL,
  `other_qualification_image` varchar(255) NOT NULL,
  `bank_account_no` varchar(255) NOT NULL,
  `bank_ifsc` varchar(255) NOT NULL,
  `person_name` varchar(255) NOT NULL,
  `person_number` varchar(255) NOT NULL,
  `person_relation` varchar(255) NOT NULL,
  `passport_size_image` varchar(255) NOT NULL,
  `employee_sign_image` varchar(255) NOT NULL,
  `aadhar_card_image` varchar(255) NOT NULL,
  `aadhar_card_back_image` varchar(255) NOT NULL,
  `pan_card_image` varchar(255) NOT NULL,
  `last_company_exp_letter_image` varchar(255) NOT NULL,
  `pay_slip_exp_letter_image` varchar(255) NOT NULL,
  `pay_slip_second_last_month_image` varchar(255) NOT NULL,
  `pay_slip_third_last_month_image` varchar(255) NOT NULL,
  `resign_mail_image` varchar(255) NOT NULL,
  `bank_stmt_last_3_mth_image` varchar(255) NOT NULL,
  `offer_letter_image` varchar(255) NOT NULL,
  `appointment_letter_image` varchar(255) NOT NULL,
  `bank_proof_image` varchar(255) NOT NULL,
  `vaccine_certificate_image` varchar(255) NOT NULL,
  `other_documents_pdf` varchar(255) NOT NULL,
  `resume_file` varchar(255) NOT NULL,
  `interview_sheet_file` varchar(255) NOT NULL,
  `special_approval_file` varchar(255) NOT NULL,
  `father_n_name` varchar(255) NOT NULL,
  `father_relation` varchar(255) NOT NULL,
  `father_dob` varchar(255) NOT NULL,
  `father_amount` varchar(255) NOT NULL,
  `mother_n_name` varchar(255) NOT NULL,
  `mother_relation` varchar(255) NOT NULL,
  `mother_dob` varchar(255) NOT NULL,
  `mother_amount` varchar(255) NOT NULL,
  `wife_n_name` varchar(255) NOT NULL,
  `wife_relation` varchar(255) NOT NULL,
  `wife_dob` varchar(255) NOT NULL,
  `wife_amount` varchar(255) NOT NULL,
  `guardian_relation` varchar(255) NOT NULL,
  `guardian_name` varchar(255) NOT NULL,
  `guardian_dob` varchar(255) NOT NULL,
  `guardian_amount` varchar(255) NOT NULL,
  `sibling_relation` varchar(255) NOT NULL,
  `sibling_name` varchar(255) NOT NULL,
  `sibling_dob` varchar(255) NOT NULL,
  `sibling_amount` varchar(255) NOT NULL,
  `child_one_name` varchar(255) NOT NULL,
  `child_one_relation` varchar(255) NOT NULL,
  `child_one_dob` varchar(255) NOT NULL,
  `child_one_amount` varchar(255) NOT NULL,
  `child_two_name` varchar(255) NOT NULL,
  `child_two_relation` varchar(255) NOT NULL,
  `child_two_dob` varchar(255) NOT NULL,
  `child_two_amount` varchar(255) NOT NULL,
  `member_included_for_insurance` varchar(255) NOT NULL,
  `request` varchar(255) NOT NULL,
  `request_generation_date` varchar(255) NOT NULL,
  `ho_request` varchar(255) NOT NULL,
  `zone_remark` varchar(255) NOT NULL,
  `ho_remark` varchar(255) NOT NULL,
  `zhr_image` varchar(255) NOT NULL,
  `head_hr_image` varchar(255) NOT NULL,
  `zhr_approved_date` varchar(255) NOT NULL,
  `hohr_approved_date` varchar(255) NOT NULL,
  `zhr_rejected_date` varchar(255) NOT NULL,
  `zone_reject_count` varchar(255) NOT NULL,
  `hohr_rejected_date` varchar(255) NOT NULL,
  `ho_reject_count` varchar(255) NOT NULL,
  `employee_id` varchar(250) NOT NULL,
  `v_work_code` varchar(255) NOT NULL,
  `time` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `grade_table`
--

CREATE TABLE `grade_table` (
  `id` int(11) NOT NULL,
  `grade` varchar(255) NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `add_users`
--
ALTER TABLE `add_users`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `branch_table`
--
ALTER TABLE `branch_table`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `check_list_table`
--
ALTER TABLE `check_list_table`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `department_table`
--
ALTER TABLE `department_table`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `designation_table`
--
ALTER TABLE `designation_table`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `employee_experience_table`
--
ALTER TABLE `employee_experience_table`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `employee_insurance_table`
--
ALTER TABLE `employee_insurance_table`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `employee_level_table`
--
ALTER TABLE `employee_level_table`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `employee_notification_table`
--
ALTER TABLE `employee_notification_table`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `employee_personal_detail`
--
ALTER TABLE `employee_personal_detail`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `grade_table`
--
ALTER TABLE `grade_table`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `add_users`
--
ALTER TABLE `add_users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `branch_table`
--
ALTER TABLE `branch_table`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `check_list_table`
--
ALTER TABLE `check_list_table`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `department_table`
--
ALTER TABLE `department_table`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `designation_table`
--
ALTER TABLE `designation_table`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `employee_experience_table`
--
ALTER TABLE `employee_experience_table`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `employee_insurance_table`
--
ALTER TABLE `employee_insurance_table`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `employee_level_table`
--
ALTER TABLE `employee_level_table`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `employee_notification_table`
--
ALTER TABLE `employee_notification_table`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `employee_personal_detail`
--
ALTER TABLE `employee_personal_detail`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `grade_table`
--
ALTER TABLE `grade_table`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
