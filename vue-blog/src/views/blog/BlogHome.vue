<template>
  <div class="blog-home">
    <div class="header">
      <div class="header-content">
        <h1 class="logo">博客系统</h1>
        <div class="user-info">
          <a-button type="primary" @click="$router.push('/blog/editor')">
            <a-icon type="edit" />写文章
          </a-button>
          <a-button type="default" @click="$router.push('/blog/batch-publish')">
            <a-icon type="upload" />批量发布
          </a-button>
          <a-dropdown>
            <div class="user-avatar">
              <a-avatar :src="avatarUrl" />
              <span class="username">{{ nickname }}</span>
            </div>
            <a-menu slot="overlay">
              <a-menu-item key="profile" @click="goToUserCenter">
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

    <!-- 科技感滚轮筛选器（包含文章列表） -->
    <div class="wheel-filter">
      <TechWheelSelector @filter-change="handleFilterChange">
        <div class="article-list-container">
          <div class="list-header">
            <a-tabs v-model="activeTab" @change="handleTabChange">
              <a-tab-pane key="recommend" tab="推荐" />
              <a-tab-pane key="latest" tab="最新" />
              <a-tab-pane key="hot" tab="热榜" />
            </a-tabs>
            <div class="filter-tags">
              <a-tag v-if="selectedDate" closable @close="clearFilter('date')">日期: {{ selectedDate }}</a-tag>
              <a-tag v-if="selectedTopic" closable @close="clearFilter('topic')">题材: {{ selectedTopic }}</a-tag>
              <a-tag v-if="selectedTheme" closable @close="clearFilter('theme')">主题: {{ selectedTheme }}</a-tag>
              <a-tag v-if="selectedAuthor" closable @close="clearFilter('author')">作者: {{ selectedAuthor }}</a-tag>
            </div>
          </div>

          <a-spin :spinning="loading">
            <div class="article-item" v-for="article in articleList" :key="article.id" @click="goToDetail(article.id)">
              <div class="article-content">
                <h3 class="article-title">{{ article.title }}</h3>
                <p class="article-summary">{{ article.summary }}</p>
                <div class="article-meta">
                  <span class="author">
                    <a-avatar size="small" :src="article.authorAvatar" />
                    {{ article.authorName }}
                  </span>
                  <span class="meta-item">
                    <a-icon type="eye" /> {{ article.viewCount }}
                  </span>
                  <span class="meta-item">
                    <a-icon type="message" /> {{ article.commentCount }}
                  </span>
                  <span class="meta-item" :class="{ 'liked': article.isLiked }" @click.stop="handleLike(article)">
                    <a-icon type="like" /> {{ article.likeCount }}
                  </span>
                  <span class="meta-item" :class="{ 'collected': article.isCollected }" @click.stop="handleCollect(article)">
                    <a-icon type="star" /> {{ article.collectCount }}
                  </span>
                </div>
              </div>
              <div class="article-cover" v-if="article.coverImage">
                <img :src="article.coverImage" alt="封面" />
              </div>
            </div>

            <div class="load-more" v-if="articleList.length < total">
              <a-button @click="loadMore" :loading="loadingMore">加载更多</a-button>
            </div>

            <a-empty v-if="!loading && articleList.length === 0" description="暂无文章" />
          </a-spin>
        </div>
      </TechWheelSelector>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { getArticleList, likeArticle, unlikeArticle, collectArticle, uncollectArticle } from '@/api/blog'
import TechWheelSelector from '@/components/TechWheelSelector.vue'
import { getAvatarUrl } from '@/utils/avatar'

export default {
  name: 'BlogHome',
  components: {
    TechWheelSelector
  },
  data() {
    return {
      loading: false,
      loadingMore: false,
      articleList: [],
      total: 0,
      pageNo: 1,
      pageSize: 10,
      activeTab: 'recommend',
      selectedDate: '',
      selectedTopic: '',
      selectedTheme: '',
      selectedAuthor: ''
    }
  },
  computed: {
    ...mapGetters('user', ['userId', 'username', 'nickname', 'avatar']),
    avatarUrl() {
      return getAvatarUrl(this.avatar)
    }
  },
  mounted() {
    this.loadArticleList()
  },
  watch: {
    userId: {
      handler(newVal) {
        if (newVal) {
          this.loadArticleList()
        }
      }
    }
  },
  methods: {
    async loadArticleList() {
      this.loading = true
      try {
        const response = await getArticleList({
          pageNo: this.pageNo,
          pageSize: this.pageSize,
          sortBy: this.activeTab === 'hot' ? 'view_count' : 'create_time',
          sortOrder: 'desc',
          date: this.selectedDate,
          topic: this.selectedTopic,
          theme: this.selectedTheme,
          author: this.selectedAuthor
        })
        
        if (response.success) {
          const data = response.result
          let articles = data.records || data
          if (this.pageNo === 1) {
            this.articleList = articles
          } else {
            this.articleList = [...this.articleList, ...articles]
          }
          this.total = data.total || this.articleList.length
          this.articleList.forEach(article => {
            article.authorAvatar = getAvatarUrl(article.authorAvatar)
          })
        }
      } catch (error) {
        this.$message.error('加载文章列表失败')
      } finally {
        this.loading = false
      }
    },
    loadMore() {
      this.pageNo++
      this.loadingMore = true
      this.loadArticleList().finally(() => {
        this.loadingMore = false
      })
    },
    handleTabChange() {
      this.pageNo = 1
      this.loadArticleList()
    },
    handleFilterChange(filters) {
      this.selectedDate = filters.date
      this.selectedTopic = filters.topic
      this.selectedTheme = filters.theme
      this.selectedAuthor = filters.author
      this.pageNo = 1
      this.loadArticleList()
    },
    clearFilter(type) {
      switch (type) {
        case 'date':
          this.selectedDate = ''
          break
        case 'topic':
          this.selectedTopic = ''
          break
        case 'theme':
          this.selectedTheme = ''
          break
        case 'author':
          this.selectedAuthor = ''
          break
      }
      this.pageNo = 1
      this.loadArticleList()
    },
    goToDetail(id) {
      this.$router.push(`/blog/article/${id}`)
    },
    async handleLike(article) {
      try {
        if (article.isLiked) {
          await unlikeArticle(article.id)
          article.likeCount--
        } else {
          await likeArticle(article.id)
          article.likeCount++
        }
        article.isLiked = !article.isLiked
      } catch (error) {
        this.$message.error('操作失败')
      }
    },
    async handleCollect(article) {
      try {
        if (article.isCollected) {
          await uncollectArticle(article.id)
          article.collectCount--
        } else {
          await collectArticle(article.id)
          article.collectCount++
        }
        article.isCollected = !article.isCollected
      } catch (error) {
        this.$message.error('操作失败')
      }
    },
    goToUserCenter() {
      if (this.userId) {
        this.$router.push(`/blog/user/${this.userId}`)
      } else {
        this.$router.push('/blog/login')
      }
    },
    handleLogout() {
      this.$store.dispatch('user/logout')
      this.$router.push('/blog/home')
    }
  }
}
</script>

<style scoped>
.blog-home {
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
  max-width: 1400px;
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

.username {
  color: #333;
}

.wheel-filter {
  padding: 20px 0;
}

.article-list-container {
  width: 100%;
  height: 100%;
  overflow-y: auto;
  padding: 20px;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 10px;
}

.filter-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.article-item {
  display: flex;
  gap: 20px;
  padding: 20px;
  margin-bottom: 20px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.3s;
}

.article-item:hover {
  background: rgba(255, 255, 255, 1);
}

.article-content {
  flex: 1;
}

.article-title {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 8px;
}

.article-title:hover {
  color: #1890ff;
}

.article-summary {
  color: #666;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.article-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 14px;
  color: #999;
}

.article-meta .author {
  display: flex;
  align-items: center;
  gap: 6px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.meta-item.liked {
  color: #ff4d4f;
}

.meta-item.collected {
  color: #faad14;
}

.article-cover {
  width: 150px;
  height: 100px;
  border-radius: 4px;
  overflow: hidden;
}

.article-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.load-more {
  text-align: center;
  margin-top: 20px;
}
</style>