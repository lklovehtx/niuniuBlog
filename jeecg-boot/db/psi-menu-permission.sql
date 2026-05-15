-- =============================================
-- Add Independent Demand Menu Permission
-- =============================================

-- ----------------------------
-- Add menu permissions (Parent: Inventory Management 1823543151335395329)
-- ----------------------------

-- Main menu
INSERT INTO `sys_permission` (
  `id`, `parent_id`, `name`, `url`, `component`, `component_name`, 
  `redirect`, `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, 
  `icon`, `is_route`, `is_leaf`, `keep_alive`, `hidden`, `hide_tab`, 
  `description`, `create_by`, `create_time`, `update_by`, `update_time`, 
  `del_flag`, `rule_flag`, `status`, `internal_or_external`, `for_desktop`, `for_mobile`
) VALUES (
  '1780627200171393101', '1823543151335395329', '独立需求', '/psi/independentDemand/list', 'psi/PsiIndependentDemandList', 'PsiIndependentDemandList', 
  NULL, 2, NULL, '1', 1.00, 0, 
  'icon-menu', 1, 1, 0, 0, 0, 
  '独立需求管理', 'admin', NOW(), NULL, NULL, 
  0, 0, '0', 0, 1, 1
);

-- Add permission
INSERT INTO `sys_permission` (
  `id`, `parent_id`, `name`, `url`, `component`, `component_name`, 
  `redirect`, `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, 
  `icon`, `is_route`, `is_leaf`, `keep_alive`, `hidden`, `hide_tab`, 
  `description`, `create_by`, `create_time`, `update_by`, `update_time`, 
  `del_flag`, `rule_flag`, `status`, `internal_or_external`, `for_desktop`, `for_mobile`
) VALUES (
  '1780627200171393102', '1780627200171393101', '添加', '', '', '', 
  NULL, 3, 'PsiIndependentDemand:add', '1', 1.00, 0, 
  '', 0, 1, 0, 0, 0, 
  '独立需求添加', 'admin', NOW(), NULL, NULL, 
  0, 0, '0', 0, 1, 1
);

-- Edit permission
INSERT INTO `sys_permission` (
  `id`, `parent_id`, `name`, `url`, `component`, `component_name`, 
  `redirect`, `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, 
  `icon`, `is_route`, `is_leaf`, `keep_alive`, `hidden`, `hide_tab`, 
  `description`, `create_by`, `create_time`, `update_by`, `update_time`, 
  `del_flag`, `rule_flag`, `status`, `internal_or_external`, `for_desktop`, `for_mobile`
) VALUES (
  '1780627200171393103', '1780627200171393101', '编辑', '', '', '', 
  NULL, 3, 'PsiIndependentDemand:edit', '1', 2.00, 0, 
  '', 0, 1, 0, 0, 0, 
  '独立需求编辑', 'admin', NOW(), NULL, NULL, 
  0, 0, '0', 0, 1, 1
);

-- Delete permission
INSERT INTO `sys_permission` (
  `id`, `parent_id`, `name`, `url`, `component`, `component_name`, 
  `redirect`, `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, 
  `icon`, `is_route`, `is_leaf`, `keep_alive`, `hidden`, `hide_tab`, 
  `description`, `create_by`, `create_time`, `update_by`, `update_time`, 
  `del_flag`, `rule_flag`, `status`, `internal_or_external`, `for_desktop`, `for_mobile`
) VALUES (
  '1780627200171393104', '1780627200171393101', '删除', '', '', '', 
  NULL, 3, 'PsiIndependentDemand:delete', '1', 3.00, 0, 
  '', 0, 1, 0, 0, 0, 
  '独立需求删除', 'admin', NOW(), NULL, NULL, 
  0, 0, '0', 0, 1, 1
);

-- Export permission
INSERT INTO `sys_permission` (
  `id`, `parent_id`, `name`, `url`, `component`, `component_name`, 
  `redirect`, `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, 
  `icon`, `is_route`, `is_leaf`, `keep_alive`, `hidden`, `hide_tab`, 
  `description`, `create_by`, `create_time`, `update_by`, `update_time`, 
  `del_flag`, `rule_flag`, `status`, `internal_or_external`, `for_desktop`, `for_mobile`
) VALUES (
  '1780627200171393105', '1780627200171393101', '导出', '', '', '', 
  NULL, 3, 'PsiIndependentDemand:exportXls', '1', 4.00, 0, 
  '', 0, 1, 0, 0, 0, 
  '独立需求导出', 'admin', NOW(), NULL, NULL, 
  0, 0, '0', 0, 1, 1
);

-- Import permission
INSERT INTO `sys_permission` (
  `id`, `parent_id`, `name`, `url`, `component`, `component_name`, 
  `redirect`, `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, 
  `icon`, `is_route`, `is_leaf`, `keep_alive`, `hidden`, `hide_tab`, 
  `description`, `create_by`, `create_time`, `update_by`, `update_time`, 
  `del_flag`, `rule_flag`, `status`, `internal_or_external`, `for_desktop`, `for_mobile`
) VALUES (
  '1780627200171393106', '1780627200171393101', '导入', '', '', '', 
  NULL, 3, 'PsiIndependentDemand:importExcel', '1', 5.00, 0, 
  '', 0, 1, 0, 0, 0, 
  '独立需求导入', 'admin', NOW(), NULL, NULL, 
  0, 0, '0', 0, 1, 1
);

-- ----------------------------
-- Add role permission associations (admin role)
-- ----------------------------
INSERT INTO `sys_role_permission` VALUES ('1780627200171393201', 'admin', '1780627200171393101');
INSERT INTO `sys_role_permission` VALUES ('1780627200171393202', 'admin', '1780627200171393102');
INSERT INTO `sys_role_permission` VALUES ('1780627200171393203', 'admin', '1780627200171393103');
INSERT INTO `sys_role_permission` VALUES ('1780627200171393204', 'admin', '1780627200171393104');
INSERT INTO `sys_role_permission` VALUES ('1780627200171393205', 'admin', '1780627200171393105');
INSERT INTO `sys_role_permission` VALUES ('1780627200171393206', 'admin', '1780627200171393106');

SELECT 'Independent Demand Menu Permission Added' AS result;