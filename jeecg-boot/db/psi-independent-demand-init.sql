-- =============================================
-- Independent Demand Feature - DB Init Script
-- =============================================

-- ----------------------------
-- Create independent demand header table
-- ----------------------------
DROP TABLE IF EXISTS `psi_independent_demand`;
CREATE TABLE `psi_independent_demand` (
  `id` varchar(36) NOT NULL COMMENT 'Primary Key',
  `demand_no` varchar(50) NOT NULL COMMENT 'Demand No',
  `company` varchar(100) DEFAULT NULL COMMENT 'Company',
  `factory` varchar(100) DEFAULT NULL COMMENT 'Factory',
  `supplier_id` varchar(36) DEFAULT NULL COMMENT 'Supplier ID',
  `supplier_name` varchar(200) DEFAULT NULL COMMENT 'Supplier Name',
  `customer_id` varchar(36) DEFAULT NULL COMMENT 'Customer ID',
  `customer_name` varchar(200) DEFAULT NULL COMMENT 'Customer Name',
  `external_no` varchar(100) DEFAULT NULL COMMENT 'External No',
  `demand_status` varchar(10) DEFAULT '0' COMMENT 'Status: 0-Draft, 1-Submitted, 2-Approved, 3-Closed',
  `create_by` varchar(36) DEFAULT NULL COMMENT 'Created By',
  `create_time` datetime DEFAULT NULL COMMENT 'Created Time',
  `update_by` varchar(36) DEFAULT NULL COMMENT 'Updated By',
  `update_time` datetime DEFAULT NULL COMMENT 'Updated Time',
  `del_flag` varchar(2) DEFAULT '0' COMMENT 'Delete Flag',
  `remark` varchar(500) DEFAULT NULL COMMENT 'Remark',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_demand_no` (`demand_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Independent Demand Header';

-- ----------------------------
-- Create independent demand item table
-- ----------------------------
DROP TABLE IF EXISTS `psi_independent_demand_item`;
CREATE TABLE `psi_independent_demand_item` (
  `id` varchar(36) NOT NULL COMMENT 'Primary Key',
  `demand_id` varchar(36) NOT NULL COMMENT 'Header ID',
  `demand_no` varchar(50) NOT NULL COMMENT 'Demand No',
  `line_no` int(11) NOT NULL COMMENT 'Line No',
  `material_id` varchar(36) DEFAULT NULL COMMENT 'Material ID',
  `material_code` varchar(100) DEFAULT NULL COMMENT 'Material Code',
  `material_name` varchar(200) DEFAULT NULL COMMENT 'Material Name',
  `factory` varchar(100) DEFAULT NULL COMMENT 'Factory',
  `required_qty` decimal(18,4) DEFAULT '0.0000' COMMENT 'Required Qty',
  `unit` varchar(50) DEFAULT NULL COMMENT 'Unit',
  `required_date` date DEFAULT NULL COMMENT 'Required Date',
  `demand_status` varchar(10) DEFAULT '0' COMMENT 'Status',
  `create_by` varchar(36) DEFAULT NULL COMMENT 'Created By',
  `create_time` datetime DEFAULT NULL COMMENT 'Created Time',
  `update_by` varchar(36) DEFAULT NULL COMMENT 'Updated By',
  `update_time` datetime DEFAULT NULL COMMENT 'Updated Time',
  `del_flag` varchar(2) DEFAULT '0' COMMENT 'Delete Flag',
  PRIMARY KEY (`id`),
  KEY `idx_demand_id` (`demand_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Independent Demand Item';

-- ----------------------------
-- Add demand status dictionary
-- ----------------------------
INSERT INTO `sys_dict` VALUES ('1780627200171393026', 'Demand Status', 'demand_status', 'Independent Demand Status', 0, 'admin', NOW(), NULL, NULL, 0);

INSERT INTO `sys_dict_item` VALUES ('1780627200171393027', '1780627200171393026', 'Draft', '0', 'Draft Status', 1, 1, 'admin', NOW(), NULL, NULL);
INSERT INTO `sys_dict_item` VALUES ('1780627200171393028', '1780627200171393026', 'Submitted', '1', 'Submitted Status', 2, 1, 'admin', NOW(), NULL, NULL);
INSERT INTO `sys_dict_item` VALUES ('1780627200171393029', '1780627200171393026', 'Approved', '2', 'Approved Status', 3, 1, 'admin', NOW(), NULL, NULL);
INSERT INTO `sys_dict_item` VALUES ('1780627200171393030', '1780627200171393026', 'Closed', '3', 'Closed Status', 4, 1, 'admin', NOW(), NULL, NULL);

SELECT 'Independent Demand DB Init Completed' AS result;