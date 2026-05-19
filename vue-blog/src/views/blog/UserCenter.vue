<template>
  <div class="user-center">
    <div class="header">
      <div class="header-content">
        <h1 class="logo" @click="$router.push('/blog/home')">博客系统</h1>
        <div class="user-info">
          <a-button type="primary" @click="$router.push('/blog/editor')">
            <a-icon type="edit" />写文章
          </a-button>
          <a-dropdown>
            <div class="user-avatar">
              <a-avatar :src="getAvatarUrl(avatar)" />
              <span class="username">{{ nickname }}</span>
            </div>
            <a-menu slot="overlay">
              <a-menu-item key="logout" @click="handleLogout">
                <a-icon type="logout" />退出登录
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </div>
      </div>
    </div>

    <div class="main-container">
      <div class="profile-section">
        <div class="profile-card">
          <a-avatar :src="getAvatarUrl(userInfo.avatar)" :size="100" />
          <h2 class="nickname">{{ userInfo.nickname || userInfo.username }}</h2>
          <p class="bio">{{ userInfo.bio || "暂无简介" }}</p>
          <div class="profile-stats">
            <div class="stat-item">
              <div class="stat-value">{{ userInfo.articleCount || 0 }}</div>
              <div class="stat-label">文章</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ userInfo.followerCount || 0 }}</div>
              <div class="stat-label">关注</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">{{ userInfo.likeCount || 0 }}</div>
              <div class="stat-label">获赞</div>
            </div>
          </div>
          <a-button v-if="isOwnProfile" type="default" @click="showEditModal">
            <a-icon type="edit" />编辑资料
          </a-button>
        </div>
      </div>

      <div class="content-section">
        <a-tabs v-model="activeTab">
          <a-tab-pane key="articles" tab="我的文章">
            <div class="article-list">
              <div
                v-for="article in articleList"
                :key="article.id"
                class="article-item"
              >
                <div class="article-info">
                  <h3 class="title" @click="goToDetail(article.id)">
                    {{ article.title }}
                  </h3>
                  <div class="meta">
                    <span class="status" :class="article.status">{{
                      article.status === "1" ? "已发布" : "草稿"
                    }}</span>
                    <span class="date">{{ article.createTime | moment }}</span>
                    <span class="views"
                      ><a-icon type="eye" /> {{ article.viewCount }}</span
                    >
                  </div>
                </div>
                <div class="article-actions" v-if="isOwnProfile">
                  <a-button
                    type="link"
                    size="small"
                    @click="editArticle(article.id)"
                    >编辑</a-button
                  >
                  <a-button
                    type="link"
                    size="small"
                    @click="deleteArticle(article.id)"
                    >删除</a-button
                  >
                </div>
              </div>
              <a-empty v-if="articleList.length === 0" description="暂无文章" />
            </div>
          </a-tab-pane>
          <a-tab-pane key="likes" tab="我的收藏">
            <div class="article-list">
              <div
                v-for="article in likeList"
                :key="article.id"
                class="article-item"
              >
                <div class="article-info">
                  <h3 class="title" @click="goToDetail(article.id)">
                    {{ article.title }}
                  </h3>
                  <div class="meta">
                    <span class="author">{{ article.authorName }}</span>
                    <span class="date">{{ article.createTime | moment }}</span>
                  </div>
                </div>
              </div>
              <a-empty v-if="likeList.length === 0" description="暂无收藏" />
            </div>
          </a-tab-pane>
        </a-tabs>
      </div>
    </div>

    <a-modal
      v-model="editModalVisible"
      title="编辑资料"
      @ok="handleSaveProfile"
      :confirmLoading="saving"
    >
      <a-form :form="form" layout="vertical">
        <a-form-item label="昵称">
          <a-input v-model="form.nickname" />
        </a-form-item>
        <a-form-item label="简介">
          <a-textarea v-model="form.bio" :rows="3" />
        </a-form-item>
        <a-form-item label="头像">
          <a-upload
            :before-upload="handleAvatarUpload"
            :show-upload-list="false"
            accept="image/*"
          >
            <a-button> <a-icon type="upload" />上传头像 </a-button>
          </a-upload>
          <img v-if="form.avatar" :src="getAvatarUrl(form.avatar)" class="avatar-preview" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script>
import { mapGetters } from "vuex";
import {
  getUserInfo,
  getUserStats,
  getArticleList,
  updateUserInfo,
  deleteArticle,
  uploadAvatar,
} from "@/api/blog";

export default {
  name: "BlogUser",
  data() {
    return {
      userInfo: {},
      articleList: [],
      likeList: [],
      activeTab: "articles",
      editModalVisible: false,
      saving: false,
      form: {
        nickname: "",
        bio: "",
        avatar: "",
      },
    };
  },
  computed: {
    ...mapGetters("user", ["userId", "username", "nickname", "avatar"]),
    isOwnProfile() {
      return !this.$route.params.id || this.$route.params.id === this.userId;
    },
  },
  methods: {
    getAvatarUrl(avatarPath) {
      if (!avatarPath) {
        return "";
      }
      // 如果已经是完整 URL，直接返回
      if (avatarPath.startsWith("http://") || avatarPath.startsWith("https://")) {
        return avatarPath;
      }
      // 如果已经是 /jeecg-boot/avatar/ 开头，直接返回
      if (avatarPath.startsWith("/jeecg-boot/avatar/")) {
        return avatarPath;
      }
      // 拼接完整 URL
      return "/jeecg-boot/avatar/" + avatarPath;
    },
    async loadUserInfo() {
      try {
        const userId = this.$route.params.id || this.userId;

        const [userResponse, statsResponse] = await Promise.all([
          getUserInfo(userId),
          getUserStats(userId)
        ]);

        if (userResponse.success) {
          this.userInfo = userResponse.result;
          // 合并统计数据，不要覆盖已有字段
          if (statsResponse.success) {
            this.userInfo = { ...this.userInfo, ...statsResponse.result };
          }
        }
      } catch (error) {
        console.error("加载用户信息失败", error);
      }
    },
    async loadArticles() {
      try {
        const response = await getArticleList({
          pageNo: 1,
          pageSize: 100,
          authorId: this.userId,
        });

        if (response.success) {
          const data = response.result;
          this.articleList = data.records || data;
        }
      } catch (error) {
        console.error("加载文章列表失败", error);
      }
    },
    showEditModal() {
      this.form = {
        nickname: this.userInfo.nickname,
        bio: this.userInfo.bio,
        avatar: this.userInfo.avatar,
      };
      this.editModalVisible = true;
    },
    async handleAvatarUpload(file) {
      try {
        const response = await uploadAvatar(file, this.userId);
        if (response.success) {
          this.form.avatar = response.result.path;
        } else {
          this.$message.error("头像上传失败");
        }
      } catch (error) {
        this.$message.error("头像上传失败");
      }
      return false;
    },
    async handleSaveProfile() {
      try {
        this.saving = true;
        const response = await updateUserInfo({
          id: this.userId,
          nickname: this.form.nickname,
          bio: this.form.bio,
          avatar: this.form.avatar,
        });

        if (response.success) {
          this.$message.success("保存成功");
          this.editModalVisible = false;
          this.loadUserInfo();
        } else {
          this.$message.error(response.message || "保存失败");
        }
      } catch (error) {
        this.$message.error("保存失败");
      } finally {
        this.saving = false;
      }
    },
    async editArticle(id) {
      this.$router.push(`/blog/editor/${id}`);
    },
    async deleteArticle(id) {
      try {
        const response = await deleteArticle(id);
        if (response.success) {
          this.$message.success("删除成功");
          this.loadArticles();
        } else {
          this.$message.error(response.message || "删除失败");
        }
      } catch (error) {
        this.$message.error("删除失败");
      }
    },
    goToDetail(id) {
      this.$router.push(`/blog/article/${id}`);
    },
    handleLogout() {
      this.$store.dispatch("user/logout").then(() => {
        this.$router.push("/login");
      });
    },
  },
  mounted() {
    this.loadUserInfo();
    this.loadArticles();
  },
  watch: {
    "$route.params.id"() {
      this.loadUserInfo();
      this.loadArticles();
    },
  },
};
</script>

<style scoped>
.user-center {
  min-height: 100vh;
  background: #f5f5f5;
}

.header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 16px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  font-size: 24px;
  font-weight: bold;
  cursor: pointer;
  margin: 0;
  color: #1890ff;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-avatar {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.username {
  margin-left: 8px;
}

.main-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
  display: grid;
  grid-template-columns: 360px 1fr;
  gap: 24px;
}

.profile-section {
  position: sticky;
  top: 100px;
  height: fit-content;
}

.profile-card {
  background: #fff;
  border-radius: 8px;
  padding: 32px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.profile-card .ant-avatar {
  margin-bottom: 16px;
}

.nickname {
  font-size: 24px;
  font-weight: bold;
  margin: 16px 0;
}

.bio {
  color: #666;
  margin: 16px 0;
  line-height: 1.6;
}

.profile-stats {
  display: flex;
  justify-content: space-around;
  margin: 24px 0;
  padding: 16px 0;
  border-top: 1px solid #f0f0f0;
  border-bottom: 1px solid #f0f0f0;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #1890ff;
}

.stat-label {
  font-size: 14px;
  color: #999;
  margin-top: 4px;
}

.content-section {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.article-list {
  min-height: 200px;
}

.article-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.3s;
}

.article-item:hover {
  background: #fafafa;
}

.article-item:last-child {
  border-bottom: none;
}

.article-info {
  flex: 1;
}

.title {
  font-size: 16px;
  margin: 0 0 8px 0;
  color: #333;
  transition: color 0.3s;
}

.title:hover {
  color: #1890ff;
}

.meta {
  display: flex;
  gap: 16px;
  font-size: 14px;
  color: #999;
}

.status {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.status.published {
  background: #e6f7ff;
  color: #1890ff;
}

.status.draft {
  background: #f5f5f5;
  color: #999;
}

.article-actions {
  display: flex;
  gap: 8px;
}

.avatar-preview {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  margin-top: 8px;
  object-fit: cover;
}
</style>
