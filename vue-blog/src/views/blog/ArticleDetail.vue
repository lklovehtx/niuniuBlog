<template>
  <div class="article-detail">
    <div class="header">
      <div class="header-content">
        <h1 class="logo" @click="$router.push('/blog/home')">博客系统</h1>
        <div class="user-info">
          <a-button type="primary" @click="$router.push('/blog/editor')">
            <a-icon type="edit" />写文章
          </a-button>
          <a-dropdown>
            <div class="user-avatar">
              <a-avatar :src="avatarUrl" />
              <span class="username">{{ nickname }}</span>
            </div>
            <a-menu slot="overlay">
              <a-menu-item
                key="profile"
                @click="$router.push(`/blog/user/${userId}`)"
              >
                <a-icon type="user" />个人中心
              </a-menu-item>
              <a-menu-divider />
              <a-menu-item key="logout" @click="handleLogout">
                <a-icon type="logout" />退出登录
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </div>
      </div>
    </div>

    <div class="main-container">
      <div class="article-container" v-if="article">
        <div class="article-header">
          <h1 class="title">{{ article.title }}</h1>
          <div class="article-info">
            <div class="author-info">
              <a-avatar :src="article.authorAvatar" />
              <div class="author-text">
                <span class="name">{{ article.authorName }}</span>
                <span class="date">{{ article.publishTime | moment }}</span>
              </div>
            </div>
            <div class="article-actions">
              <a-button-group>
                <a-button
                  @click="handleLike"
                  :type="article.isLiked ? 'primary' : 'default'"
                >
                  <a-icon type="like" /> {{ article.likeCount }}
                </a-button>
                <a-button
                  @click="handleCollect"
                  :type="article.isCollected ? 'primary' : 'default'"
                >
                  <a-icon type="star" /> {{ article.collectCount }}
                </a-button>
              </a-button-group>
              <a-button v-if="isAuthor" type="default" @click="handleEdit">
                <a-icon type="edit" />编辑
              </a-button>
            </div>
          </div>
        </div>

        <div class="article-cover" v-if="article.coverImage">
          <img :src="article.coverImage" alt="封面" />
        </div>

        <div class="article-content" v-html="article.content"></div>

        <div class="article-tags" v-if="article.tags">
          <a-tag
            v-for="tag in article.tags.split(',')"
            :key="tag"
            color="blue"
            >{{ tag }}</a-tag
          >
        </div>

        <div class="article-footer">
          <div class="like-section" @click="handleLike">
            <a-icon
              type="like"
              :theme="article.isLiked ? 'filled' : 'outlined'"
            />
            <span>{{ article.likeCount }} 点赞</span>
          </div>
          <div class="collect-section" @click="handleCollect">
            <a-icon
              type="star"
              :theme="article.isCollected ? 'filled' : 'outlined'"
            />
            <span>{{ article.collectCount }} 收藏</span>
          </div>
        </div>
      </div>

      <div class="comment-container">
        <h3 class="comment-title">评论 ({{ commentList.length }})</h3>

        <div class="comment-input" v-if="canComment">
          <a-textarea
            v-model="newComment"
            placeholder="写下你的评论..."
            :rows="3"
          />
          <div class="comment-actions">
            <a-button
              type="primary"
              @click="submitComment"
              :loading="submitting"
            >
              发布评论
            </a-button>
          </div>
        </div>

        <div
          class="comment-verify-tip"
          v-else-if="isLoggedIn && !emailVerified"
        >
          <p>邮箱未验证，请先验证邮箱</p>
          <a-button type="primary" @click="$router.push('/blog/verify-email')">
            验证邮箱
          </a-button>
        </div>

        <div class="comment-login-tip" v-else>
          <p>登录后即可发表评论</p>
          <a-button type="primary" @click="$router.push('/blog/login')">
            登录
          </a-button>
        </div>

        <div class="comment-list">
          <div
            v-for="comment in commentList"
            :key="comment.id"
            class="comment-item"
          >
            <div class="comment-avatar">
              <a-avatar :src="comment.commentatorAvatar" />
            </div>
            <div class="comment-body">
              <div class="comment-header">
                <span class="commentator">{{ comment.commentatorName }}</span>
                <span class="comment-time">{{
                  comment.createTime | moment
                }}</span>
              </div>
              <div class="comment-content">{{ comment.content }}</div>
              <div class="comment-actions">
                <span @click="handleCommentLike(comment)">
                  <a-icon
                    type="like"
                    :theme="comment.isLiked ? 'filled' : 'outlined'"
                  />
                  {{ comment.likeCount }}
                </span>
                <span @click="handleReply(comment)">回复</span>
                <span
                  v-if="isCommentAuthor(comment)"
                  @click="handleDeleteComment(comment)"
                  >删除</span
                >
              </div>

              <div
                v-if="comment.replies && comment.replies.length"
                class="comment-replies"
              >
                <div
                  v-for="reply in comment.replies"
                  :key="reply.id"
                  class="reply-item"
                >
                  <a-avatar size="small" :src="reply.commentatorAvatar" />
                  <div class="reply-body">
                    <span class="commentator">{{ reply.commentatorName }}</span>
                    <span v-if="reply.replyToName" class="reply-to"
                      >回复 @{{ reply.replyToName }}</span
                    >
                    <span class="comment-content">{{ reply.content }}</span>
                  </div>
                </div>
              </div>

              <div v-if="comment.showReplyInput" class="reply-input">
                <a-input
                  v-model="comment.replyContent"
                  placeholder="写下你的回复..."
                  @pressEnter="submitReply(comment)"
                />
                <a-button
                  size="small"
                  type="primary"
                  @click="submitReply(comment)"
                  >回复</a-button
                >
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters } from "vuex";
import {
  getArticleDetail,
  likeArticle,
  unlikeArticle,
  collectArticle,
  uncollectArticle,
  getCommentList,
  addComment,
} from "@/api/blog";
import { getAvatarUrl } from "@/utils/avatar";

export default {
  name: "BlogArticle",
  data() {
    return {
      article: null,
      commentList: [],
      newComment: "",
      submitting: false,
    };
  },
  computed: {
    ...mapGetters("user", [
      "userId",
      "username",
      "nickname",
      "avatar",
      "emailVerified",
    ]),
    avatarUrl() {
      return getAvatarUrl(this.avatar);
    },
    isAuthor() {
      return this.article && this.article.authorId === this.userId;
    },
    isLoggedIn() {
      return !!this.userId;
    },
    canComment() {
      return this.isLoggedIn && this.emailVerified;
    },
  },
  mounted() {
    this.loadArticle();
    this.loadComments();
  },
  watch: {
    userId: {
      handler() {
        this.loadArticle();
        this.loadComments();
      },
    },
  },
  methods: {
    async loadArticle() {
      try {
        const articleId = this.$route.params.id;

        // 调用后端 API 获取文章详情
        const response = await getArticleDetail(articleId);
        if (response.success) {
          this.article = response.result;
          // 处理文章作者头像URL
          if (
            this.article.authorAvatar &&
            !this.article.authorAvatar.startsWith("http")
          ) {
            this.article.authorAvatar =
              "http://localhost:8080/jeecg-boot/avatar/" +
              this.article.authorAvatar;
          }
        } else {
          // 如果后端没有数据，使用模拟数据
          const mockArticle = {
            id: articleId,
            title: "如何使用 Vue.js 构建现代化 Web 应用",
            content: "<p>这是文章的内容...</p><p>这是第二段内容...</p>",
            summary: "这是文章的摘要",
            coverImage: "",
            authorId: "1",
            authorName: "张三",
            authorAvatar: "",
            publishTime: new Date().toISOString(),
            viewCount: 1,
            likeCount: 89,
            collectCount: 23,
            commentCount: 45,
            isLiked: false,
            isCollected: false,
            tags: "Vue,前端,技术",
          };
          this.article = mockArticle;
        }
      } catch (error) {
        this.$message.error("加载文章失败");
      }
    },
    async loadComments() {
      try {
        const response = await getCommentList(this.$route.params.id);
        if (response.success) {
          this.commentList = response.result;
          this.commentList.forEach((comment) => {
            comment.commentatorAvatar = getAvatarUrl(comment.commentatorAvatar);
            if (comment.replies && comment.replies.length) {
              comment.replies.forEach((reply) => {
                reply.commentatorAvatar = getAvatarUrl(reply.commentatorAvatar);
              });
            }
          });
        } else {
          this.commentList = [];
        }
      } catch (error) {
        console.error("加载评论失败", error);
      }
    },
    async handleLike() {
      try {
        if (!this.isLoggedIn) {
          this.$message.error("请先登录");
          return;
        }

        if (this.article.isLiked) {
          // 取消点赞
          const response = await unlikeArticle(this.article.id);
          if (response.success) {
            this.article.likeCount -= 1;
            this.article.isLiked = false;
          } else {
            this.$message.error(response.message || "取消点赞失败");
          }
        } else {
          // 点赞
          const response = await likeArticle(this.article.id);
          if (response.success) {
            this.article.likeCount += 1;
            this.article.isLiked = true;
          } else {
            this.$message.error(response.message || "点赞失败");
          }
        }
      } catch (error) {
        this.$message.error("操作失败");
      }
    },
    async handleCollect() {
      try {
        if (!this.isLoggedIn) {
          this.$message.error("请先登录");
          return;
        }

        if (this.article.isCollected) {
          // 取消收藏
          const response = await uncollectArticle(this.article.id);
          if (response.success) {
            this.article.collectCount -= 1;
            this.article.isCollected = false;
          } else {
            this.$message.error(response.message || "取消收藏失败");
          }
        } else {
          // 收藏
          const response = await collectArticle(this.article.id);
          if (response.success) {
            this.article.collectCount += 1;
            this.article.isCollected = true;
          } else {
            this.$message.error(response.message || "收藏失败");
          }
        }
      } catch (error) {
        this.$message.error("操作失败");
      }
    },
    handleEdit() {
      this.$router.push(`/blog/editor/${this.article.id}`);
    },
    handleLogout() {
      this.$store.dispatch("user/logout");
      this.$router.push("/blog/home");
    },
    async submitComment() {
      if (!this.newComment.trim()) return;

      this.submitting = true;
      try {
        // 创建新评论对象
        const commentData = {
          articleId: this.$route.params.id,
          content: this.newComment,
        };

        // 调用后端 API 添加评论
        const response = await addComment(commentData);
        if (response.success) {
          const newComment = response.result;
          newComment.showReplyInput = false;
          newComment.replyContent = "";
          newComment.replies = [];

          // 更新本地列表
          this.commentList.unshift(newComment);
          this.$message.success("评论成功");
          this.newComment = "";
        } else {
          this.$message.error(response.message || "评论失败");
        }
      } catch (error) {
        this.$message.error("评论失败");
      } finally {
        this.submitting = false;
      }
    },
    handleReply(comment) {
      comment.showReplyInput = !comment.showReplyInput;
    },
    async submitReply(comment) {
      if (!comment.replyContent.trim()) return;

      try {
        const newReply = {
          id: Date.now().toString(),
          content: comment.replyContent,
          commentatorId: this.userId,
          commentatorName: this.nickname,
          commentatorAvatar: getAvatarUrl(this.avatar),
          createTime: new Date().toISOString(),
          replyToId: comment.commentatorId,
          replyToName: comment.commentatorName,
        };
        if (!comment.replies) {
          comment.replies = [];
        }
        comment.replies.push(newReply);
        this.$message.success("回复成功");
        comment.replyContent = "";
        comment.showReplyInput = false;
      } catch (error) {
        this.$message.error("回复失败");
      }
    },
    isCommentAuthor(comment) {
      return comment.commentatorId === this.userId;
    },
    async handleDeleteComment(comment) {
      try {
        // 模拟删除评论
        const index = this.commentList.findIndex((c) => c.id === comment.id);
        if (index > -1) {
          this.commentList.splice(index, 1);
        }
        this.$message.success("删除成功");
      } catch (error) {
        this.$message.error("删除失败");
      }
    },
    handleCommentLike(comment) {
      comment.isLiked = !comment.isLiked;
      comment.likeCount += comment.isLiked ? 1 : -1;
    },
  },
};
</script>

<style scoped>
.article-detail {
  min-height: 100vh;
  background: #f5f5f5;
}

.header {
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 20px;
  height: 60px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  font-size: 24px;
  color: #1890ff;
  margin: 0;
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-avatar {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.main-container {
  max-width: 1000px;
  margin: 20px auto;
  padding: 0 20px;
}

.article-container {
  background: white;
  border-radius: 8px;
  padding: 40px;
  margin-bottom: 20px;
}

.article-header {
  margin-bottom: 30px;
}

.title {
  font-size: 32px;
  font-weight: 700;
  color: #1a1a1a;
  margin-bottom: 20px;
  line-height: 1.4;
}

.article-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.author-text {
  display: flex;
  flex-direction: column;
}

.author-text .name {
  font-size: 16px;
  color: #333;
  font-weight: 500;
}

.author-text .date {
  font-size: 14px;
  color: #999;
}

.article-actions {
  display: flex;
  gap: 12px;
}

.article-cover {
  margin-bottom: 30px;
  border-radius: 8px;
  overflow: hidden;
}

.article-cover img {
  width: 100%;
  max-height: 400px;
  object-fit: cover;
}

.article-content {
  font-size: 16px;
  line-height: 1.8;
  color: #333;
  margin-bottom: 30px;
}

.article-tags {
  margin-bottom: 30px;
}

.article-footer {
  display: flex;
  gap: 30px;
  padding-top: 30px;
  border-top: 1px solid #f0f0f0;
}

.like-section,
.collect-section {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #666;
  font-size: 16px;
}

.like-section:hover,
.collect-section:hover {
  color: #1890ff;
}

.comment-container {
  background: white;
  border-radius: 8px;
  padding: 30px;
}

.comment-title {
  font-size: 20px;
  margin-bottom: 20px;
}

.comment-input {
  margin-bottom: 30px;
}

.comment-login-tip {
  text-align: center;
  padding: 30px;
  background: #f5f5f5;
  border-radius: 8px;
  margin-bottom: 30px;
}

.comment-login-tip p {
  margin-bottom: 15px;
  color: #666;
  font-size: 14px;
}

.comment-actions {
  margin-top: 12px;
  text-align: right;
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.comment-item {
  display: flex;
  gap: 12px;
}

.comment-body {
  flex: 1;
}

.comment-header {
  margin-bottom: 8px;
}

.commentator {
  font-weight: 500;
  color: #333;
  margin-right: 10px;
}

.comment-time {
  font-size: 12px;
  color: #999;
}

.comment-content {
  color: #333;
  line-height: 1.6;
  margin-bottom: 8px;
}

.comment-actions {
  display: flex;
  gap: 16px;
  font-size: 14px;
  color: #999;
}

.comment-actions span {
  cursor: pointer;
}

.comment-actions span:hover {
  color: #1890ff;
}

.comment-replies {
  margin-top: 12px;
  padding-left: 20px;
  border-left: 2px solid #f0f0f0;
}

.reply-item {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.reply-body {
  flex: 1;
}

.reply-to {
  color: #666;
  margin: 0 4px;
}

.reply-input {
  margin-top: 12px;
  display: flex;
  gap: 8px;
}
</style>
