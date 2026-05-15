/*
Date: 2024-07-15 23:05:40
*/

-- ----------------------------
-- Records of bas_bank_account
-- ----------------------------
INSERT IGNORE INTO `bas_bank_account` (`id`, `account_no`, `name`, `currency`, `init_bal`, `bank_no`, `bank_address`, `manager`, `remark`, `attachment`, `is_enabled`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('1584913699556106242', '12345678901234567890', '基本户', 'CNY', 0.00, '1', NULL, NULL, NULL, NULL, 1, 'admin', '2022-10-25 22:24:07', 'admin', '2022-10-25 22:28:52', NULL);
INSERT IGNORE INTO `bas_bank_account` (`id`, `account_no`, `name`, `currency`, `init_bal`, `bank_no`, `bank_address`, `manager`, `remark`, `attachment`, `is_enabled`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('1584915128429649922', '22345678901234567890', '往来户', 'CNY', 0.00, NULL, NULL, NULL, NULL, NULL, 1, 'admin', '2022-10-25 22:29:48', NULL, NULL, NULL);

-- ----------------------------
-- Records of bas_biz_period
-- ----------------------------
UPDATE `bas_biz_period` SET `begin_year`=2025, `begin_month`=11, `year`=2025, `month`=11 WHERE `id`='ba43143c9fc711eab66f18dbf2568723';

-- ----------------------------
-- Records of bas_bom
-- ----------------------------
INSERT IGNORE INTO `bas_bom` (`id`, `code`, `name`, `aux_name`, `material_id`, `unit_id`, `is_enabled`, `remark`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('1778772143294771202', 'BOM1', '组装台式机', 'BOM1 组装台式机', '1778767768795082753', '1778764510106087426', 1, NULL, 'admin', '2025-11-01 21:08:22', 'admin', '2025-11-01 21:15:23', NULL);

-- ----------------------------
-- Records of bas_bom_entry
-- ----------------------------
INSERT IGNORE INTO `bas_bom_entry` (`id`, `mid`, `entry_no`, `material_id`, `unit_id`, `qty`, `cost_rate`, `remark`, `version`) VALUES ('1778772143370268673', '1778772143294771202', 1, '1778769060653297666', '1778764510106087426', 1.000000, 80.000000, '', NULL);
INSERT IGNORE INTO `bas_bom_entry` (`id`, `mid`, `entry_no`, `material_id`, `unit_id`, `qty`, `cost_rate`, `remark`, `version`) VALUES ('1778772143449960450', '1778772143294771202', 2, '1778769564619894786', '1778764510106087426', 1.000000, 16.000000, '', NULL);
INSERT IGNORE INTO `bas_bom_entry` (`id`, `mid`, `entry_no`, `material_id`, `unit_id`, `qty`, `cost_rate`, `remark`, `version`) VALUES ('1778772143479320578', '1778772143294771202', 3, '1778769740600307713', '1778769151694860289', 1.000000, 4.000000, '', NULL);

-- ----------------------------
-- Records of bas_customer
-- ----------------------------
INSERT IGNORE INTO `bas_customer` (`id`, `code`, `name`, `short_name`, `aux_name`, `customer_category`, `customer_level`, `tax_scale`, `credit_quota`, `headquarters`, `area`, `biz_area`, `address`, `website`, `legal_person`, `legal_person_phone`, `financial_contacts`, `financial_phone`, `invoice_company`, `invoice_tax_code`, `invoice_bank_name`, `invoice_bank_code`, `invoice_account`, `invoice_phone`, `invoice_address`, `payment_company`, `payment_bank_name`, `payment_bank_code`, `payment_account`, `recv_name`, `recv_phone`, `recv_fax`, `recv_email`, `recv_address`, `recv_postcode`, `attachment`, `is_enabled`, `remark`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('1778760082821083137', 'C001', '北京客户1', NULL, 'C001 北京客户1', '1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, NULL, 'admin', '2025-11-01 20:20:26', NULL, NULL, NULL);
INSERT IGNORE INTO `bas_customer` (`id`, `code`, `name`, `short_name`, `aux_name`, `customer_category`, `customer_level`, `tax_scale`, `credit_quota`, `headquarters`, `area`, `biz_area`, `address`, `website`, `legal_person`, `legal_person_phone`, `financial_contacts`, `financial_phone`, `invoice_company`, `invoice_tax_code`, `invoice_bank_name`, `invoice_bank_code`, `invoice_account`, `invoice_phone`, `invoice_address`, `payment_company`, `payment_bank_name`, `payment_bank_code`, `payment_account`, `recv_name`, `recv_phone`, `recv_fax`, `recv_email`, `recv_address`, `recv_postcode`, `attachment`, `is_enabled`, `remark`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('1778760368356716545', 'C002', '北京客户2', NULL, 'C002 北京客户2', '1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, NULL, 'admin', '2025-11-01 20:21:34', NULL, NULL, NULL);

-- ----------------------------
-- Records of bas_material
-- ----------------------------
INSERT IGNORE INTO `bas_material` (`id`, `category_id`, `code`, `name`, `aux_name`, `barcode`, `is_enabled`, `model`, `unit_id`, `sale_price`, `tax_code`, `is_sn`, `remark`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('1778763427682054146', '1778762450929315842', '02HW', '华为Mate60', '02HW 华为Mate60', '02HW', '1', '8G运行内存，128G机内存储', '1778764372784574466', NULL, NULL, '0', NULL, 'admin', '2025-11-01 20:33:44', 'admin', '2024-09-27 20:59:32', NULL);
INSERT IGNORE INTO `bas_material` (`id`, `category_id`, `code`, `name`, `aux_name`, `barcode`, `is_enabled`, `model`, `unit_id`, `sale_price`, `tax_code`, `is_sn`, `remark`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('1778765283502190593', '1778762450929315842', '02XM', '小米Redmi', '02XM 小米Redmi', '02XM', '1', '12G运行内存，128G机内存储', '1778764372784574466', NULL, NULL, '0', NULL, 'admin', '2025-11-01 20:41:06', 'admin', '2024-09-27 20:59:46', NULL);
INSERT IGNORE INTO `bas_material` (`id`, `category_id`, `code`, `name`, `aux_name`, `barcode`, `is_enabled`, `model`, `unit_id`, `sale_price`, `tax_code`, `is_sn`, `remark`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('1778767768795082753', '1778762351385899010', '01DIY01', '组装机', '01DIY01 组装机', '01DIY01', '1', 'i7 16G内存 1T固态盘 23.8英寸LED', '1778764510106087426', NULL, NULL, '0', NULL, 'admin', '2025-11-01 20:50:59', 'admin', '2024-07-15 22:51:47', NULL);
INSERT IGNORE INTO `bas_material` (`id`, `category_id`, `code`, `name`, `aux_name`, `barcode`, `is_enabled`, `model`, `unit_id`, `sale_price`, `tax_code`, `is_sn`, `remark`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('1778769060653297666', '1778762351385899010', 'P0101', '主机', 'P0101 主机', 'P0101', '1', 'i7 16G内存 1T固态盘', '1778764510106087426', NULL, NULL, '0', NULL, 'admin', '2025-11-01 20:56:07', 'admin', '2024-07-15 22:51:14', NULL);
INSERT IGNORE INTO `bas_material` (`id`, `category_id`, `code`, `name`, `aux_name`, `barcode`, `is_enabled`, `model`, `unit_id`, `sale_price`, `tax_code`, `is_sn`, `remark`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('1778769564619894786', '1778762351385899010', 'P0102', '显示器', 'P0102 显示器', 'P0102', '1', '23.8英寸LED', '1778764510106087426', NULL, NULL, '0', NULL, 'admin', '2025-11-01 20:58:07', 'admin', '2024-07-15 22:50:22', NULL);
INSERT IGNORE INTO `bas_material` (`id`, `category_id`, `code`, `name`, `aux_name`, `barcode`, `is_enabled`, `model`, `unit_id`, `sale_price`, `tax_code`, `is_sn`, `remark`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('1778769740600307713', '1778762351385899010', 'P0103', '键鼠', 'P0103 键鼠', 'P0103', '1', NULL, '1778769151694860289', NULL, NULL, '0', NULL, 'admin', '2025-11-01 20:58:49', 'admin', '2024-07-15 22:50:52', NULL);
INSERT IGNORE INTO `bas_material` (`id`, `category_id`, `code`, `name`, `aux_name`, `barcode`, `is_enabled`, `model`, `unit_id`, `sale_price`, `tax_code`, `is_sn`, `remark`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('1812863517967060993', '1812861498720063490', '1101', '矿泉水', '1101 矿泉水', '1101', '1', '500毫升', '1812862967020064770', NULL, NULL, '0', NULL, 'admin', '2024-07-15 22:55:19', NULL, NULL, NULL);
INSERT IGNORE INTO `bas_material` (`id`, `category_id`, `code`, `name`, `aux_name`, `barcode`, `is_enabled`, `model`, `unit_id`, `sale_price`, `tax_code`, `is_sn`, `remark`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('1812863803397836801', '1812861498720063490', '1102', '方便面', '1102 方便面', '1102', '1', '128克', '1812863200319836162', NULL, NULL, '0', NULL, 'admin', '2024-07-15 22:56:27', NULL, NULL, NULL);

-- ----------------------------
-- Records of bas_material_category
-- ----------------------------
INSERT IGNORE INTO `bas_material_category` (`id`, `pid`, `has_child`, `code`, `name`, `fullname`, `is_enabled`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('1778762351385899010', '0', '0', '01', '电脑', NULL, 1, 'admin', '2025-11-01 20:29:27', NULL, NULL, NULL);
INSERT IGNORE INTO `bas_material_category` (`id`, `pid`, `has_child`, `code`, `name`, `fullname`, `is_enabled`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('1778762450929315842', '0', '0', '02', '手机', NULL, 1, 'admin', '2025-11-01 20:29:51', NULL, NULL, NULL);
INSERT IGNORE INTO `bas_material_category` (`id`, `pid`, `has_child`, `code`, `name`, `fullname`, `is_enabled`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('1812861498720063490', '0', '0', '11', '食品饮料', NULL, 1, 'admin', '2024-07-15 22:47:17', NULL, NULL, NULL);
INSERT IGNORE INTO `bas_material_category` (`id`, `pid`, `has_child`, `code`, `name`, `fullname`, `is_enabled`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('1812861545553661954', '0', '0', '99', '其他', NULL, 1, 'admin', '2024-07-15 22:47:28', NULL, NULL, NULL);

-- ----------------------------
-- Records of bas_sequence
-- ----------------------------
INSERT IGNORE INTO `bas_sequence` (`k`, `v`) VALUES ('CGAP-251101-', 1);
INSERT IGNORE INTO `bas_sequence` (`k`, `v`) VALUES ('CGDD-251101-', 1);
INSERT IGNORE INTO `bas_sequence` (`k`, `v`) VALUES ('CGFK-251101-', 2);
INSERT IGNORE INTO `bas_sequence` (`k`, `v`) VALUES ('CGFKSQ-251101-', 1);
INSERT IGNORE INTO `bas_sequence` (`k`, `v`) VALUES ('CGFP-251101-', 1);
INSERT IGNORE INTO `bas_sequence` (`k`, `v`) VALUES ('CGRK-251101-', 1);
INSERT IGNORE INTO `bas_sequence` (`k`, `v`) VALUES ('XSAR-251101-', 1);
INSERT IGNORE INTO `bas_sequence` (`k`, `v`) VALUES ('XSCK-251101-', 1);
INSERT IGNORE INTO `bas_sequence` (`k`, `v`) VALUES ('XSDD-251101-', 1);
INSERT IGNORE INTO `bas_sequence` (`k`, `v`) VALUES ('XSFP-251101-', 1);
INSERT IGNORE INTO `bas_sequence` (`k`, `v`) VALUES ('XSSK-251101-', 1);
INSERT IGNORE INTO `bas_sequence` (`k`, `v`) VALUES ('YFHX-251101-', 1);
INSERT IGNORE INTO `bas_sequence` (`k`, `v`) VALUES ('YSHX-251101-', 1);

-- ----------------------------
-- Records of bas_supplier
-- ----------------------------
INSERT IGNORE INTO `bas_supplier` (`id`, `code`, `name`, `short_name`, `aux_name`, `supplier_category`, `supplier_level`, `tax_scale`, `headquarters`, `area`, `biz_area`, `address`, `website`, `legal_person`, `legal_person_phone`, `financial_contacts`, `financial_phone`, `invoice_company`, `invoice_tax_code`, `invoice_bank_name`, `invoice_bank_code`, `invoice_account`, `invoice_phone`, `invoice_address`, `receipt_company`, `receipt_bank_name`, `receipt_bank_code`, `receipt_account`, `recv_name`, `recv_phone`, `recv_fax`, `recv_email`, `recv_address`, `recv_postcode`, `attachment`, `is_enabled`, `alter_suppliers`, `remark`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('1778760613778026497', 'S001', '手机供应商1', NULL, 'S001 手机供应商1', '1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, 'admin', '2025-11-01 20:22:33', NULL, NULL, NULL);
INSERT IGNORE INTO `bas_supplier` (`id`, `code`, `name`, `short_name`, `aux_name`, `supplier_category`, `supplier_level`, `tax_scale`, `headquarters`, `area`, `biz_area`, `address`, `website`, `legal_person`, `legal_person_phone`, `financial_contacts`, `financial_phone`, `invoice_company`, `invoice_tax_code`, `invoice_bank_name`, `invoice_bank_code`, `invoice_account`, `invoice_phone`, `invoice_address`, `receipt_company`, `receipt_bank_name`, `receipt_bank_code`, `receipt_account`, `recv_name`, `recv_phone`, `recv_fax`, `recv_email`, `recv_address`, `recv_postcode`, `attachment`, `is_enabled`, `alter_suppliers`, `remark`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('1778760764231905281', 'S002', '电脑供应商1', NULL, 'S002 电脑供应商1', '1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, 'admin', '2025-11-01 20:23:09', NULL, NULL, NULL);
INSERT IGNORE INTO `bas_supplier` (`id`, `code`, `name`, `short_name`, `aux_name`, `supplier_category`, `supplier_level`, `tax_scale`, `headquarters`, `area`, `biz_area`, `address`, `website`, `legal_person`, `legal_person_phone`, `financial_contacts`, `financial_phone`, `invoice_company`, `invoice_tax_code`, `invoice_bank_name`, `invoice_bank_code`, `invoice_account`, `invoice_phone`, `invoice_address`, `receipt_company`, `receipt_bank_name`, `receipt_bank_code`, `receipt_account`, `recv_name`, `recv_phone`, `recv_fax`, `recv_email`, `recv_address`, `recv_postcode`, `attachment`, `is_enabled`, `alter_suppliers`, `remark`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('1812864540488044546', 'S101', '食品公司', NULL, 'S101 食品公司', '1', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, 'admin', '2024-07-15 22:59:22', NULL, NULL, NULL);

-- ----------------------------
-- Records of bas_unit
-- ----------------------------
INSERT IGNORE INTO `bas_unit` (`id`, `name`, `symbol`, `has_child`, `pid`, `factor`, `is_enabled`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('1242301004380692481', '米', 'm', '1', '0', 1.000000, 1, 'admin', '2020-03-24 12:03:35', NULL, NULL, NULL);
INSERT IGNORE INTO `bas_unit` (`id`, `name`, `symbol`, `has_child`, `pid`, `factor`, `is_enabled`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('1242301191434067970', '厘米', 'cm', '0', '1242301004380692481', 0.010000, 1, 'admin', '2020-03-24 12:04:20', NULL, NULL, NULL);
INSERT IGNORE INTO `bas_unit` (`id`, `name`, `symbol`, `has_child`, `pid`, `factor`, `is_enabled`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('1242647149539823618', '千米', 'km', '0', '1242301004380692481', 1000.000000, 1, 'admin', '2020-03-25 10:59:02', 'admin', '2022-10-25 23:47:24', NULL);
INSERT IGNORE INTO `bas_unit` (`id`, `name`, `symbol`, `has_child`, `pid`, `factor`, `is_enabled`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('1584918494836760577', '立方米', ' m³', '1', '0', 1.000000, 1, 'admin', '2022-10-25 22:43:10', 'admin', '2022-10-25 22:48:16', NULL);
INSERT IGNORE INTO `bas_unit` (`id`, `name`, `symbol`, `has_child`, `pid`, `factor`, `is_enabled`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('1584919780650983426', '升', 'L', '0', '1584918494836760577', 0.001000, 1, 'admin', '2022-10-25 22:48:17', NULL, NULL, NULL);
INSERT IGNORE INTO `bas_unit` (`id`, `name`, `symbol`, `has_child`, `pid`, `factor`, `is_enabled`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('1778764372784574466', '个', '个', '0', '0', 1.000000, 1, 'admin', '2025-11-01 20:37:29', NULL, NULL, NULL);
INSERT IGNORE INTO `bas_unit` (`id`, `name`, `symbol`, `has_child`, `pid`, `factor`, `is_enabled`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('1778764510106087426', '台', '台', '0', '0', 1.000000, 1, 'admin', '2025-11-01 20:38:02', NULL, NULL, NULL);
INSERT IGNORE INTO `bas_unit` (`id`, `name`, `symbol`, `has_child`, `pid`, `factor`, `is_enabled`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('1778769151694860289', '套', '套', '0', '0', 1.000000, 1, 'admin', '2025-11-01 20:56:28', NULL, NULL, NULL);
INSERT IGNORE INTO `bas_unit` (`id`, `name`, `symbol`, `has_child`, `pid`, `factor`, `is_enabled`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('1812862967020064770', '瓶', '瓶', '0', '0', 1.000000, 1, 'admin', '2024-07-15 22:53:07', NULL, NULL, NULL);
INSERT IGNORE INTO `bas_unit` (`id`, `name`, `symbol`, `has_child`, `pid`, `factor`, `is_enabled`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('1812863200319836162', '袋', '袋', '0', '0', 1.000000, 1, 'admin', '2024-07-15 22:54:03', NULL, NULL, NULL);
INSERT IGNORE INTO `bas_unit` (`id`, `name`, `symbol`, `has_child`, `pid`, `factor`, `is_enabled`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('40288101710a4c6201710a65913d0002', '千克', 'kg', '1', '0', 1.000000, 1, 'admin', '2020-03-23 00:00:00', 'admin', '2022-10-25 23:47:12', NULL);
INSERT IGNORE INTO `bas_unit` (`id`, `name`, `symbol`, `has_child`, `pid`, `factor`, `is_enabled`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('40288101710a4c6201710a6618cc0003', '吨', 't', '0', '40288101710a4c6201710a65913d0002', 1000.000000, 1, 'admin', '2020-03-24 10:36:35', 'admin', '2020-03-24 10:37:04', NULL);

-- ----------------------------
-- Records of bas_warehouse
-- ----------------------------
INSERT IGNORE INTO `bas_warehouse` (`id`, `pid`, `has_child`, `code`, `name`, `aux_name`, `phone`, `is_enabled`, `remark`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('1778761054754566145', '0', '1', '01', '一库', '01 一库', NULL, 1, NULL, 'admin', '2025-11-01 20:24:18', 'admin', '2025-11-01 20:25:11', NULL);
INSERT IGNORE INTO `bas_warehouse` (`id`, `pid`, `has_child`, `code`, `name`, `aux_name`, `phone`, `is_enabled`, `remark`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('1778761280643002369', '1778761054754566145', '0', '01.01', '一库一区', '01.01 一库一区', NULL, 1, NULL, 'admin', '2025-11-01 20:25:12', NULL, NULL, NULL);
INSERT IGNORE INTO `bas_warehouse` (`id`, `pid`, `has_child`, `code`, `name`, `aux_name`, `phone`, `is_enabled`, `remark`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('1778761637657964546', '1778761054754566145', '0', '01.02', '一库二区', '01.02 一库二区', NULL, 1, NULL, 'admin', '2025-11-01 20:26:37', NULL, NULL, NULL);
INSERT IGNORE INTO `bas_warehouse` (`id`, `pid`, `has_child`, `code`, `name`, `aux_name`, `phone`, `is_enabled`, `remark`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('1778762080614215681', '0', '0', '02', '二库', '02 二库', NULL, 1, NULL, 'admin', '2025-11-01 20:28:22', NULL, NULL, NULL);

-- ----------------------------
-- Records of fin_payable
-- ----------------------------
INSERT IGNORE INTO `fin_payable` (`id`, `bill_no`, `bill_date`, `src_bill_type`, `src_bill_id`, `src_no`, `subject`, `is_rubric`, `payable_type`, `supplier_id`, `op_dept`, `operator`, `amt`, `checked_amt`, `attachment`, `remark`, `is_auto`, `bill_stage`, `approver`, `bpmi_instance_id`, `approval_result_type`, `approval_remark`, `is_effective`, `effective_time`, `is_closed`, `is_voided`, `sys_org_code`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('1778782184865394689', 'CGAP-251101-001', '2025-11-01 00:00:00', 'StkIo:101', '1778782139952787457', 'CGRK-251101-001', NULL, 0, '201', '1778760613778026497', 'A01A05', 'zhagnxiao', 800000.00, 800000.00, NULL, NULL, 1, '34', NULL, NULL, NULL, NULL, 1, '2025-11-01 21:48:16', 1, 0, 'A01A03', 'admin', '2025-11-01 21:48:16', 'admin', '2025-11-01 22:16:38', NULL);

-- ----------------------------
-- Records of fin_payable_check
-- ----------------------------
INSERT IGNORE INTO `fin_payable_check` (`id`, `bill_no`, `bill_date`, `src_bill_type`, `src_bill_id`, `src_no`, `subject`, `is_rubric`, `payable_check_type`, `supplier_id`, `amt`, `attachment`, `remark`, `is_auto`, `bill_stage`, `approver`, `bpmi_instance_id`, `approval_result_type`, `approval_remark`, `is_effective`, `effective_time`, `is_closed`, `is_voided`, `create_time`, `create_by`, `sys_org_code`, `update_time`, `update_by`, `version`) VALUES ('1778789323155566594', 'YFHX-251101-001', '2025-11-01', 'FinPayment:2021', '1778789277932580865', 'CGFK-251101-002', NULL, 0, '2', '1778760613778026497', 800000.00, NULL, NULL, 1, '34', NULL, NULL, NULL, NULL, 1, '2025-11-01 22:16:38', 1, 0, '2025-11-01 22:16:38', 'admin', 'A01A03', '2025-11-01 22:16:38', 'admin', NULL);

-- ----------------------------
-- Records of fin_payable_check_entry
-- ----------------------------
INSERT IGNORE INTO `fin_payable_check_entry` (`id`, `mid`, `bill_no`, `entry_no`, `src_bill_type`, `src_bill_id`, `src_entry_id`, `src_no`, `check_side`, `amt`, `remark`, `custom1`, `custom2`, `version`) VALUES ('1778789323382059009', '1778789323155566594', 'YFHX-251101-001', 101, 'FinPayable:201', '1778782184865394689', NULL, 'CGAP-251101-001', '1', 800000.00, NULL, NULL, NULL, NULL);
INSERT IGNORE INTO `fin_payable_check_entry` (`id`, `mid`, `bill_no`, `entry_no`, `src_bill_type`, `src_bill_id`, `src_entry_id`, `src_no`, `check_side`, `amt`, `remark`, `custom1`, `custom2`, `version`) VALUES ('1778789323424002050', '1778789323155566594', 'YFHX-251101-001', 201, 'FinPayment:2021', '1778789277932580865', NULL, 'CGFK-251101-002', '2', 800000.00, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Records of fin_payment
-- ----------------------------
INSERT IGNORE INTO `fin_payment` (`id`, `bill_no`, `bill_date`, `src_bill_type`, `src_bill_id`, `src_no`, `subject`, `is_rubric`, `payment_type`, `supplier_id`, `amt`, `checked_amt`, `attachment`, `remark`, `is_auto`, `bill_stage`, `approver`, `bpmi_instance_id`, `approval_result_type`, `approval_remark`, `is_effective`, `effective_time`, `is_closed`, `is_voided`, `create_time`, `create_by`, `sys_org_code`, `update_time`, `update_by`, `version`) VALUES ('1778789277932580865', 'CGFK-251101-002', '2025-11-01', '', '', '', NULL, 0, '2021', '1778760613778026497', 800000.00, 800000.00, NULL, NULL, 0, '34', 'admin', NULL, '1', NULL, 1, '2025-11-01 22:16:37', 1, 0, '2025-11-01 22:16:27', 'admin', 'A01A03', '2025-11-01 22:16:38', 'admin', NULL);

-- ----------------------------
-- Records of fin_payment_entry
-- ----------------------------
INSERT IGNORE INTO `fin_payment_entry` (`id`, `mid`, `bill_no`, `entry_no`, `src_bill_type`, `src_bill_id`, `src_entry_id`, `src_no`, `settle_method`, `bank_account_id`, `amt`, `remark`, `custom1`, `custom2`, `version`) VALUES ('1778789277991301122', '1778789277932580865', 'CGFK-251101-002', 10, 'FinPaymentReq:2021', '1778787910413316098', NULL, 'CGFKSQ-251101-001', '', '', 800000.00, '', '', '', NULL);

-- ----------------------------
-- Records of fin_payment_req
-- ----------------------------
INSERT IGNORE INTO `fin_payment_req` (`id`, `bill_no`, `bill_date`, `src_bill_type`, `src_bill_id`, `src_no`, `subject`, `is_rubric`, `payment_type`, `supplier_id`, `op_dept`, `operator`, `amt`, `paid_amt`, `attachment`, `remark`, `is_auto`, `bill_stage`, `approver`, `bpmi_instance_id`, `approval_result_type`, `approval_remark`, `is_effective`, `effective_time`, `is_closed`, `is_voided`, `sys_org_code`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('1778787910413316098', 'CGFKSQ-251101-001', '2025-11-01', '', '', '', NULL, 0, '2021', '1778760613778026497', 'A01A05', 'zhagnxiao', 800000.00, 800000.00, NULL, NULL, 0, '34', 'admin', NULL, '1', NULL, 1, '2025-11-01 22:11:10', 1, 0, 'A01A03', 'admin', '2025-11-01 22:11:01', 'admin', '2025-11-01 22:16:37', NULL);

-- ----------------------------
-- Records of fin_payment_req_entry
-- ----------------------------
INSERT IGNORE INTO `fin_payment_req_entry` (`id`, `mid`, `bill_no`, `entry_no`, `src_bill_type`, `src_bill_id`, `src_entry_id`, `src_no`, `amt`, `paid_amt`, `remark`, `custom1`, `custom2`, `version`) VALUES ('1778787910534950913', '1778787910413316098', 'CGFKSQ-251101-001', 10, 'StkIo:101', '1778782139952787457', NULL, 'CGRK-251101-001', 800000.00, 800000.00, '', '', '', NULL);

-- ----------------------------
-- Records of fin_pur_invoice
-- ----------------------------
INSERT IGNORE INTO `fin_pur_invoice` (`id`, `bill_no`, `bill_date`, `src_bill_type`, `src_bill_id`, `src_no`, `subject`, `is_rubric`, `is_returned`, `invoice_type`, `blue_invoice_no`, `invoice_no`, `invoice_date`, `supplier_id`, `amt`, `attachment`, `remark`, `is_auto`, `bill_stage`, `approver`, `bpmi_instance_id`, `approval_result_type`, `approval_remark`, `is_effective`, `effective_time`, `is_closed`, `is_voided`, `sys_org_code`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('1778792016641126401', 'CGFP-251101-001', '2025-11-01', 'StkIo:101', '', '', NULL, 0, 0, '12', NULL, '1', NULL, '1778760613778026497', 800000.00, NULL, NULL, 0, '34', 'admin', NULL, '1', NULL, 1, '2025-11-01 22:27:30', 1, 0, 'A01A03', 'admin', '2025-11-01 22:27:20', 'admin', '2025-11-01 22:27:31', NULL);

-- ----------------------------
-- Records of fin_pur_invoice_entry
-- ----------------------------
INSERT IGNORE INTO `fin_pur_invoice_entry` (`id`, `mid`, `bill_no`, `entry_no`, `src_bill_type`, `src_bill_id`, `src_entry_id`, `src_no`, `material_id`, `unit_id`, `tax_rate`, `qty`, `amt`, `remark`, `custom1`, `custom2`, `version`) VALUES ('1778792016678875137', '1778792016641126401', 'CGFP-251101-001', 10, 'StkIo:101', '1778782139952787457', '1778782140032479233', 'CGRK-251101-001:10', '1778763427682054146', '1778764372784574466', 13.0000, 100.000000, 400000.00, '', '', '', NULL);
INSERT IGNORE INTO `fin_pur_invoice_entry` (`id`, `mid`, `bill_no`, `entry_no`, `src_bill_type`, `src_bill_id`, `src_entry_id`, `src_no`, `material_id`, `unit_id`, `tax_rate`, `qty`, `amt`, `remark`, `custom1`, `custom2`, `version`) VALUES ('1778792016733401090', '1778792016641126401', 'CGFP-251101-001', 20, 'StkIo:101', '1778782139952787457', '1778782140107976705', 'CGRK-251101-001:20', '1778765283502190593', '1778764372784574466', 13.0000, 200.000000, 400000.00, '', '', '', NULL);

-- ----------------------------
-- Records of fin_receipt
-- ----------------------------
INSERT IGNORE INTO `fin_receipt` (`id`, `bill_no`, `bill_date`, `src_bill_type`, `src_bill_id`, `src_no`, `subject`, `is_rubric`, `receipt_type`, `customer_id`, `amt`, `checked_amt`, `attachment`, `remark`, `is_auto`, `bill_stage`, `approver`, `bpmi_instance_id`, `approval_result_type`, `approval_remark`, `is_effective`, `effective_time`, `is_closed`, `is_voided`, `sys_org_code`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('1778791604965994497', 'XSSK-251101-001', '2025-11-01 00:00:00', '', '', '', NULL, 0, '102', '1778760082821083137', 250000.00, 250000.00, NULL, NULL, 0, '34', 'admin', NULL, '1', NULL, 1, '2025-11-01 22:25:53', 1, 0, 'A01A03', 'admin', '2025-11-01 22:25:42', 'admin', '2025-11-01 22:25:54', NULL);

-- ----------------------------
-- Records of fin_receipt_entry
-- ----------------------------
INSERT IGNORE INTO `fin_receipt_entry` (`id`, `mid`, `bill_no`, `entry_no`, `src_bill_type`, `src_bill_id`, `src_entry_id`, `src_no`, `settle_method`, `bank_account_id`, `amt`, `remark`, `custom1`, `custom2`, `version`) VALUES ('1778791605012131841', '1778791604965994497', 'XSSK-251101-001', 10, 'StkIo:201', '1778791008804401153', NULL, 'XSCK-251101-001', '', '', 250000.00, '', '', '', NULL);

-- ----------------------------
-- Records of fin_receipt_req
-- ----------------------------

-- ----------------------------
-- Records of fin_receipt_req_entry
-- ----------------------------

-- ----------------------------
-- Records of fin_receivable
-- ----------------------------
INSERT IGNORE INTO `fin_receivable` (`id`, `bill_no`, `bill_date`, `src_bill_type`, `src_bill_id`, `src_no`, `subject`, `is_rubric`, `receivable_type`, `customer_id`, `op_dept`, `operator`, `amt`, `checked_amt`, `attachment`, `remark`, `is_auto`, `bill_stage`, `approver`, `bpmi_instance_id`, `approval_result_type`, `approval_remark`, `is_effective`, `effective_time`, `is_closed`, `is_voided`, `sys_org_code`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('1778791049191354369', 'XSAR-251101-001', '2025-11-01 00:00:00', 'StkIo:201', '1778791008804401153', 'XSCK-251101-001', NULL, 0, '101', '1778760082821083137', 'A01A03', 'liyewu', 250000.00, 250000.00, NULL, NULL, 1, '34', NULL, NULL, NULL, NULL, 1, '2025-11-01 22:23:29', 1, 0, 'A01A03', 'admin', '2025-11-01 22:23:29', 'admin', '2025-11-01 22:25:54', NULL);

-- ----------------------------
-- Records of fin_receivable_check
-- ----------------------------
INSERT IGNORE INTO `fin_receivable_check` (`id`, `bill_no`, `bill_date`, `src_bill_type`, `src_bill_id`, `src_no`, `subject`, `is_rubric`, `receivable_check_type`, `customer_id`, `amt`, `attachment`, `remark`, `is_auto`, `bill_stage`, `approver`, `bpmi_instance_id`, `approval_result_type`, `approval_remark`, `is_effective`, `effective_time`, `is_closed`, `is_voided`, `create_time`, `create_by`, `sys_org_code`, `update_time`, `update_by`, `version`) VALUES ('1778791653401817089', 'YSHX-251101-001', '2025-11-01', 'FinReceipt:102', '1778791604965994497', 'XSSK-251101-001', NULL, 0, '1', '1778760082821083137', 250000.00, NULL, NULL, 1, '34', NULL, NULL, NULL, NULL, 1, '2025-11-01 22:25:53', 1, 0, '2025-11-01 22:25:53', 'admin', 'A01A03', '2025-11-01 22:25:54', 'admin', NULL);

-- ----------------------------
-- Records of fin_receivable_check_entry
-- ----------------------------
INSERT IGNORE INTO `fin_receivable_check_entry` (`id`, `mid`, `bill_no`, `entry_no`, `src_bill_type`, `src_bill_id`, `src_entry_id`, `src_no`, `check_side`, `amt`, `remark`, `custom1`, `custom2`, `version`) VALUES ('1778791655662546946', '1778791653401817089', 'YSHX-251101-001', 101, 'FinReceivable:101', '1778791049191354369', NULL, 'XSAR-251101-001', '1', 250000.00, NULL, NULL, NULL, NULL);
INSERT IGNORE INTO `fin_receivable_check_entry` (`id`, `mid`, `bill_no`, `entry_no`, `src_bill_type`, `src_bill_id`, `src_entry_id`, `src_no`, `check_side`, `amt`, `remark`, `custom1`, `custom2`, `version`) VALUES ('1778791655700295682', '1778791653401817089', 'YSHX-251101-001', 201, 'FinReceipt:102', '1778791604965994497', NULL, 'XSSK-251101-001', '2', 250000.00, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Records of fin_sal_invoice
-- ----------------------------
INSERT IGNORE INTO `fin_sal_invoice` (`id`, `bill_no`, `bill_date`, `src_bill_type`, `src_bill_id`, `src_no`, `subject`, `is_rubric`, `is_returned`, `invoice_type`, `blue_invoice_no`, `invoice_no`, `invoice_date`, `customer_id`, `amt`, `attachment`, `remark`, `is_auto`, `bill_stage`, `approver`, `bpmi_instance_id`, `approval_result_type`, `approval_remark`, `is_effective`, `effective_time`, `is_closed`, `is_voided`, `sys_org_code`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('1778792209914654721', 'XSFP-251101-001', '2025-11-01', 'StkIo:201', '', '', NULL, 0, 0, '12', NULL, '1', NULL, '1778760082821083137', 250000.00, NULL, NULL, 0, '34', 'admin', NULL, '1', NULL, 1, '2025-11-01 22:28:16', 1, 0, 'A01A03', 'admin', '2025-11-01 22:28:06', 'admin', '2025-11-01 22:28:16', NULL);

-- ----------------------------
-- Records of fin_sal_invoice_entry
-- ----------------------------
INSERT IGNORE INTO `fin_sal_invoice_entry` (`id`, `mid`, `bill_no`, `entry_no`, `src_bill_type`, `src_bill_id`, `src_entry_id`, `src_no`, `material_id`, `unit_id`, `tax_rate`, `qty`, `amt`, `remark`, `custom1`, `custom2`, `version`) VALUES ('1778792209939820545', '1778792209914654721', 'XSFP-251101-001', 10, 'StkIo:201', '1778791008804401153', '1778791008829566977', 'XSCK-251101-001:10', '1778763427682054146', '1778764372784574466', 13.0000, 50.000000, 250000.00, '', '', '', NULL);

-- ----------------------------
-- Records of pur_compare
-- ----------------------------


-- ----------------------------
-- Records of pur_compare_entry
-- ----------------------------

-- ----------------------------
-- Records of pur_inquiry
-- ----------------------------

-- ----------------------------
-- Records of pur_inquiry_entry
-- ----------------------------

-- ----------------------------
-- Records of pur_order
-- ----------------------------
INSERT IGNORE INTO `pur_order` (`id`, `bill_no`, `bill_date`, `src_bill_type`, `src_bill_id`, `src_no`, `subject`, `is_rubric`, `pur_type`, `supplier_id`, `contact`, `phone`, `fax`, `email`, `op_dept`, `operator`, `delivery_method`, `delivery_place`, `delivery_time`, `transport_method`, `payment_method`, `settle_method`, `settle_time`, `invoice_method`, `invoice_type`, `currency`, `exchange_rate`, `qty`, `amt`, `prepayment_bal`, `settle_qty`, `settle_amt`, `in_qty`, `in_cost`, `settled_amt`, `invoiced_amt`, `attachment`, `remark`, `is_auto`, `bill_stage`, `approver`, `bpmi_instance_id`, `approval_result_type`, `approval_remark`, `is_effective`, `effective_time`, `is_closed`, `is_voided`, `sys_org_code`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('1778780566212177921', 'CGDD-251101-001', '2025-11-01', '', '', '', NULL, 0, NULL, '1778760613778026497', NULL, NULL, NULL, NULL, 'A01A05', 'zhagnxiao', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '12', NULL, NULL, 300.000000, 800000.00, 0.00, 300.000000, 800000.00, 300.000000, 800000.00, 800000.00, 800000.00, NULL, NULL, 0, '34', 'admin', NULL, '1', NULL, 1, '2025-11-01 21:45:53', 1, 0, 'A01A03', 'admin', '2025-11-01 21:41:50', 'admin', '2025-11-01 22:27:30', NULL);

-- ----------------------------
-- Records of pur_order_entry
-- ----------------------------
INSERT IGNORE INTO `pur_order_entry` (`id`, `mid`, `bill_no`, `entry_no`, `src_bill_type`, `src_bill_id`, `src_entry_id`, `src_no`, `material_id`, `unit_id`, `qty`, `tax_rate`, `price`, `discount_rate`, `tax`, `amt`, `in_qty`, `in_cost`, `settle_qty`, `settle_amt`, `invoiced_qty`, `invoiced_amt`, `remark`, `custom1`, `custom2`, `version`) VALUES ('1778780566442864641', '1778780566212177921', 'CGDD-251101-001', 10, '', NULL, NULL, '', '1778763427682054146', '1778764372784574466', 100.000000, 13.000000, 4000.000000, 100.0000, 46017.70, 400000.00, 100.000000, 400000.00, 100.000000, 400000.00, 100.000000, 400000.00, '', '', '', NULL);
INSERT IGNORE INTO `pur_order_entry` (`id`, `mid`, `bill_no`, `entry_no`, `src_bill_type`, `src_bill_id`, `src_entry_id`, `src_no`, `material_id`, `unit_id`, `qty`, `tax_rate`, `price`, `discount_rate`, `tax`, `amt`, `in_qty`, `in_cost`, `settle_qty`, `settle_amt`, `invoiced_qty`, `invoiced_amt`, `remark`, `custom1`, `custom2`, `version`) VALUES ('1778780566442864642', '1778780566212177921', 'CGDD-251101-001', 20, '', NULL, NULL, '', '1778765283502190593', '1778764372784574466', 200.000000, 13.000000, 2000.000000, 100.0000, 46017.70, 400000.00, 200.000000, 400000.00, 200.000000, 400000.00, 200.000000, 400000.00, '', '', '', NULL);

-- ----------------------------
-- Records of pur_quot
-- ----------------------------

-- ----------------------------
-- Records of pur_quot_entry
-- ----------------------------

-- ----------------------------
-- Records of pur_req
-- ----------------------------

-- ----------------------------
-- Records of pur_req_entry
-- ----------------------------

-- ----------------------------
-- Records of sal_order
-- ----------------------------
INSERT IGNORE INTO `sal_order` (`id`, `bill_no`, `bill_date`, `src_bill_type`, `src_bill_id`, `src_no`, `subject`, `is_rubric`, `customer_id`, `contact`, `phone`, `fax`, `email`, `op_dept`, `operator`, `delivery_method`, `delivery_place`, `delivery_time`, `transport_method`, `payment_method`, `settle_method`, `settle_time`, `invoice_method`, `invoice_type`, `currency`, `exchange_rate`, `qty`, `amt`, `prereceipt_bal`, `out_qty`, `out_cost`, `settle_qty`, `settle_amt`, `settled_amt`, `invoiced_amt`, `ex_tax_settle_amt`, `gross_profit`, `attachment`, `remark`, `is_auto`, `bill_stage`, `approver`, `bpmi_instance_id`, `approval_result_type`, `approval_remark`, `is_effective`, `effective_time`, `is_closed`, `is_voided`, `sys_org_code`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('1778790735675518977', 'XSDD-251101-001', '2025-11-01', '', '', '', NULL, 0, '1778760082821083137', NULL, NULL, NULL, NULL, 'A01A03', 'liyewu', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '12', NULL, NULL, 50.000000, 250000.00, 0.00, 50.000000, 200000.00, 50.000000, 250000.00, 250000.00, 250000.00, 221238.94, 44247.79, NULL, NULL, 0, '34', 'admin', NULL, '1', NULL, 1, '2025-11-01 22:22:29', 1, 0, 'A01A03', 'admin', '2025-11-01 22:22:14', 'admin', '2025-11-01 22:28:16', NULL);

-- ----------------------------
-- Records of sal_order_entry
-- ----------------------------
INSERT IGNORE INTO `sal_order_entry` (`id`, `mid`, `bill_no`, `entry_no`, `src_bill_type`, `src_bill_id`, `src_entry_id`, `src_no`, `material_id`, `unit_id`, `qty`, `tax_rate`, `price`, `discount_rate`, `tax`, `amt`, `out_qty`, `out_cost`, `settle_qty`, `settle_amt`, `invoiced_qty`, `invoiced_amt`, `ex_tax_settle_amt`, `gross_profit`, `remark`, `custom1`, `custom2`, `version`) VALUES ('1778790735746822145', '1778790735675518977', 'XSDD-251101-001', 10, NULL, NULL, NULL, '', '1778763427682054146', '1778764372784574466', 50.000000, 13.000000, 5000.000000, 100.0000, 28761.06, 250000.00, 50.000000, 200000.00, 50.000, 250000.00, 50.000000, 250000.00, 221238.94, 44247.79, '', '', '', NULL);

-- ----------------------------
-- Records of sal_quot
-- ----------------------------

-- ----------------------------
-- Records of sal_quot_entry
-- ----------------------------

-- ----------------------------
-- Records of stk_check
-- ----------------------------


-- ----------------------------
-- Records of stk_check_entry
-- ----------------------------

-- ----------------------------
-- Records of stk_check_single
-- ----------------------------

-- ----------------------------
-- Records of stk_inventory
-- ----------------------------
INSERT IGNORE INTO `stk_inventory` (`id`, `warehouse_id`, `material_id`, `batch_no`, `unit_id`, `qty`, `cost`, `is_single_supplier`, `supplier_id`, `is_closed`, `sys_org_code`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('1778782184181723137', '1778761280643002369', '1778763427682054146', '0', '1778764372784574466', 50.000000, 200000.00, 0, '1778760613778026497', 0, 'A01A03', 'admin', '2025-11-01 21:48:16', 'admin', '2025-11-01 22:23:29', 0);
INSERT IGNORE INTO `stk_inventory` (`id`, `warehouse_id`, `material_id`, `batch_no`, `unit_id`, `qty`, `cost`, `is_single_supplier`, `supplier_id`, `is_closed`, `sys_org_code`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('1778782184433381378', '1778761637657964546', '1778765283502190593', '0', '1778764372784574466', 200.000000, 400000.00, 0, '1778760613778026497', 0, 'A01A03', 'admin', '2025-11-01 21:48:16', NULL, NULL, 0);

-- ----------------------------
-- Records of stk_inventory_single
-- ----------------------------

-- ----------------------------
-- Records of stk_io
-- ----------------------------
INSERT IGNORE INTO `stk_io` (`id`, `bill_no`, `bill_date`, `src_bill_type`, `src_bill_id`, `src_no`, `bom_id`, `subject`, `is_rubric`, `stock_io_type`, `op_dept`, `operator`, `handler`, `has_rp`, `has_swell`, `supplier_id`, `customer_id`, `invoice_type`, `cost`, `settle_amt`, `settled_amt`, `invoiced_amt`, `attachment`, `remark`, `has_single`, `is_auto`, `bill_stage`, `approver`, `bpmi_instance_id`, `approval_result_type`, `approval_remark`, `is_effective`, `effective_time`, `is_closed`, `is_voided`, `sys_org_code`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('1778782139952787457', 'CGRK-251101-001', '2025-11-01 00:00:00', 'PurOrder', '1778780566212177921', 'CGDD-251101-001', NULL, NULL, 0, '101', 'A01A05', 'zhagnxiao', NULL, 1, 0, '1778760613778026497', NULL, '12', 800000.00, 800000.00, 800000.00, 800000.00, NULL, NULL, 0, 0, '34', 'admin', NULL, '1', NULL, 1, '2025-11-01 21:48:15', 1, 0, 'A01A03', 'admin', '2025-11-01 21:48:05', 'admin', '2025-11-01 22:27:30', NULL);
INSERT IGNORE INTO `stk_io` (`id`, `bill_no`, `bill_date`, `src_bill_type`, `src_bill_id`, `src_no`, `bom_id`, `subject`, `is_rubric`, `stock_io_type`, `op_dept`, `operator`, `handler`, `has_rp`, `has_swell`, `supplier_id`, `customer_id`, `invoice_type`, `cost`, `settle_amt`, `settled_amt`, `invoiced_amt`, `attachment`, `remark`, `has_single`, `is_auto`, `bill_stage`, `approver`, `bpmi_instance_id`, `approval_result_type`, `approval_remark`, `is_effective`, `effective_time`, `is_closed`, `is_voided`, `sys_org_code`, `create_by`, `create_time`, `update_by`, `update_time`, `version`) VALUES ('1778791008804401153', 'XSCK-251101-001', '2025-11-01 00:00:00', 'SalOrder', '1778790735675518977', 'XSDD-251101-001', NULL, NULL, 0, '201', 'A01A03', 'liyewu', NULL, 1, 0, NULL, '1778760082821083137', '12', 200000.00, 250000.00, 250000.00, 250000.00, NULL, NULL, 0, 0, '34', 'admin', NULL, '1', NULL, 1, '2025-11-01 22:23:29', 1, 0, 'A01A03', 'admin', '2025-11-01 22:23:19', 'admin', '2025-11-01 22:28:16', NULL);

-- ----------------------------
-- Records of stk_io_entry
-- ----------------------------
INSERT IGNORE INTO `stk_io_entry` (`id`, `mid`, `bill_no`, `entry_no`, `src_bill_type`, `src_bill_id`, `src_entry_id`, `src_no`, `material_id`, `batch_no`, `warehouse_id`, `stock_io_direction`, `supplier_id`, `unit_id`, `swell_qty`, `qty`, `expense`, `cost`, `settle_qty`, `tax_rate`, `price`, `discount_rate`, `tax`, `settle_amt`, `invoiced_qty`, `invoiced_amt`, `remark`, `custom1`, `custom2`, `version`) VALUES ('1778782140032479233', '1778782139952787457', 'CGRK-251101-001', 10, 'PurOrder', '1778780566212177921', '1778780566442864641', 'CGDD-251101-001:10', '1778763427682054146', '0', '1778761280643002369', '1', '1778760613778026497', '1778764372784574466', 0.000000, 100.000000, 0.00, 400000.000000, 100.000000, 13.0000, 4000.0000, 100.0000, 46017.70, 400000.00, 100.000000, 400000.00, '', '', '', NULL);
INSERT IGNORE INTO `stk_io_entry` (`id`, `mid`, `bill_no`, `entry_no`, `src_bill_type`, `src_bill_id`, `src_entry_id`, `src_no`, `material_id`, `batch_no`, `warehouse_id`, `stock_io_direction`, `supplier_id`, `unit_id`, `swell_qty`, `qty`, `expense`, `cost`, `settle_qty`, `tax_rate`, `price`, `discount_rate`, `tax`, `settle_amt`, `invoiced_qty`, `invoiced_amt`, `remark`, `custom1`, `custom2`, `version`) VALUES ('1778782140107976705', '1778782139952787457', 'CGRK-251101-001', 20, 'PurOrder', '1778780566212177921', '1778780566442864642', 'CGDD-251101-001:20', '1778765283502190593', '0', '1778761637657964546', '1', '1778760613778026497', '1778764372784574466', 0.000000, 200.000000, 0.00, 400000.000000, 200.000000, 13.0000, 2000.0000, 100.0000, 46017.70, 400000.00, 200.000000, 400000.00, '', '', '', NULL);
INSERT IGNORE INTO `stk_io_entry` (`id`, `mid`, `bill_no`, `entry_no`, `src_bill_type`, `src_bill_id`, `src_entry_id`, `src_no`, `material_id`, `batch_no`, `warehouse_id`, `stock_io_direction`, `supplier_id`, `unit_id`, `swell_qty`, `qty`, `expense`, `cost`, `settle_qty`, `tax_rate`, `price`, `discount_rate`, `tax`, `settle_amt`, `invoiced_qty`, `invoiced_amt`, `remark`, `custom1`, `custom2`, `version`) VALUES ('1778791008829566977', '1778791008804401153', 'XSCK-251101-001', 10, 'SalOrder', '1778790735675518977', '1778790735746822145', 'XSDD-251101-001:10', '1778763427682054146', '0', '1778761280643002369', '2', '', '1778764372784574466', 0.000000, 50.000000, 0.00, 200000.000000, 50.000000, 13.0000, 5000.0000, 100.0000, 28761.06, 250000.00, 50.000000, 250000.00, '', '', '', NULL);

-- ----------------------------
-- Records of stk_io_single
-- ----------------------------


-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT IGNORE INTO `sys_user` (`id`, `username`, `realname`, `password`, `salt`, `avatar`, `birthday`, `sex`, `email`, `phone`, `org_code`, `status`, `del_flag`, `activiti_sync`, `work_no`, `post`, `telephone`, `create_by`, `create_time`, `update_by`, `update_time`, `user_identity`, `depart_ids`, `rel_tenant_ids`, `client_id`) VALUES ('1818658310336843778', 'psi', '进销存用户', 'e0eaf10d3284282a', 'I5lWqul2', NULL, NULL, NULL, NULL, '13800000000', 'A01', '1', '0', '1', '20', '', NULL, 'admin', '2024-07-31 22:41:45', 'admin', '2025-12-22 20:31:27', '1', '', NULL, NULL);
INSERT IGNORE INTO `sys_user` (`id`, `username`, `realname`, `password`, `salt`, `avatar`, `birthday`, `sex`, `email`, `phone`, `org_code`, `status`, `del_flag`, `activiti_sync`, `work_no`, `post`, `telephone`, `create_by`, `create_time`, `update_by`, `update_time`, `user_identity`, `depart_ids`, `rel_tenant_ids`, `client_id`) VALUES ('2003011396046700546', 'psi1', '制单人', '4fed7410e791f687', 'MYXd33ro', NULL, NULL, NULL, NULL, '13800000001', 'A01A05', '1', '0', '1', '21', '', NULL, 'admin', '2025-12-22 15:55:06', 'psi1', '2025-12-22 16:13:59', '1', '', NULL, NULL);
INSERT IGNORE INTO `sys_user` (`id`, `username`, `realname`, `password`, `salt`, `avatar`, `birthday`, `sex`, `email`, `phone`, `org_code`, `status`, `del_flag`, `activiti_sync`, `work_no`, `post`, `telephone`, `create_by`, `create_time`, `update_by`, `update_time`, `user_identity`, `depart_ids`, `rel_tenant_ids`, `client_id`) VALUES ('2003012793936920577', 'psi2', '审核人', '0f8319023f242400', 'VRri0Mbc', NULL, NULL, NULL, NULL, '13800000002', 'A01A05', '1', '0', '1', '22', '', NULL, 'admin', '2025-12-22 16:00:39', 'psi2', '2025-12-22 16:14:34', '1', '', NULL, NULL);
INSERT IGNORE INTO `sys_user` (`id`, `username`, `realname`, `password`, `salt`, `avatar`, `birthday`, `sex`, `email`, `phone`, `org_code`, `status`, `del_flag`, `activiti_sync`, `work_no`, `post`, `telephone`, `create_by`, `create_time`, `update_by`, `update_time`, `user_identity`, `depart_ids`, `rel_tenant_ids`, `client_id`) VALUES ('2003013351796129793', 'psi3', '仓管员', '22c21eb66fce4f41', 'kUovICqY', NULL, NULL, NULL, NULL, '13800000003', 'A01A07', '1', '0', '1', '23', '', NULL, 'admin', '2025-12-22 16:02:52', 'admin', '2025-12-22 21:33:47', '1', '', NULL, NULL);


-- ----------------------------
-- Records of sys_user_depart
-- ----------------------------
-- 查询：SELECT * FROM sys_user_depart WHERE user_id IN ('1818658310336843778', '2003011396046700546', '2003012793936920577', '2003013351796129793')
INSERT IGNORE INTO `sys_user_depart` (`ID`, `user_id`, `dep_id`) VALUES ('2003080943407742978', '1818658310336843778', 'c6d7cb4deeac411cb3384b1b31278596');
INSERT IGNORE INTO `sys_user_depart` (`ID`, `user_id`, `dep_id`) VALUES ('2003015719610761217', '2003011396046700546', '1702003807585611778');
INSERT IGNORE INTO `sys_user_depart` (`ID`, `user_id`, `dep_id`) VALUES ('2003015766914121730', '2003012793936920577', '1702003807585611778');
INSERT IGNORE INTO `sys_user_depart` (`ID`, `user_id`, `dep_id`) VALUES ('2003096628162842626', '2003013351796129793', '2003011927838310402');

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
-- 查询：SELECT * FROM sys_user_role WHERE user_id IN ('1818658310336843778', '2003011396046700546', '2003012793936920577', '2003013351796129793')
INSERT IGNORE INTO `sys_user_role` (`id`, `user_id`, `role_id`) VALUES ('2003080943365799938', '1818658310336843778', '1607404823218536450');
INSERT IGNORE INTO `sys_user_role` (`id`, `user_id`, `role_id`) VALUES ('2003080943374188545', '1818658310336843778', '1607406089571188738');
INSERT IGNORE INTO `sys_user_role` (`id`, `user_id`, `role_id`) VALUES ('2003080943382577153', '1818658310336843778', '1607406978935603201');
INSERT IGNORE INTO `sys_user_role` (`id`, `user_id`, `role_id`) VALUES ('2003080943382577154', '1818658310336843778', '1607407527064027138');
INSERT IGNORE INTO `sys_user_role` (`id`, `user_id`, `role_id`) VALUES ('2003080943382577155', '1818658310336843778', '1993691832873127938');
INSERT IGNORE INTO `sys_user_role` (`id`, `user_id`, `role_id`) VALUES ('2003015719585595393', '2003011396046700546', '1607404823218536450');
INSERT IGNORE INTO `sys_user_role` (`id`, `user_id`, `role_id`) VALUES ('2003015719585595394', '2003011396046700546', '1993691832873127938');
INSERT IGNORE INTO `sys_user_role` (`id`, `user_id`, `role_id`) VALUES ('2003015766880567298', '2003012793936920577', '1607406089571188738');
INSERT IGNORE INTO `sys_user_role` (`id`, `user_id`, `role_id`) VALUES ('2003015766888955906', '2003012793936920577', '1607407527064027138');
INSERT IGNORE INTO `sys_user_role` (`id`, `user_id`, `role_id`) VALUES ('2003015766888955907', '2003012793936920577', '1993691832873127938');
INSERT IGNORE INTO `sys_user_role` (`id`, `user_id`, `role_id`) VALUES ('2003096628141871105', '2003013351796129793', '1993691832873127938');
