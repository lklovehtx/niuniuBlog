export function getAvatarUrl(avatarPath) {
  if (!avatarPath || avatarPath === '') {
    return null
  }
  // 如果已经是完整URL，直接返回
  if (avatarPath.startsWith('http://') || avatarPath.startsWith('https://')) {
    return avatarPath
  }
  // 如果已经是 /jeecg-boot/avatar/ 开头，直接返回
  if (avatarPath.startsWith('/jeecg-boot/avatar/')) {
    return avatarPath
  }
  // 如果是相对路径，转换为完整URL
  return '/jeecg-boot/avatar/' + avatarPath
}
