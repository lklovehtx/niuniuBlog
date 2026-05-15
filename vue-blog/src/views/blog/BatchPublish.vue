<template>
  <div class="batch-publish">
    <div class="header">
      <h1 class="title">批量发布文章</h1>
      <a-button type="primary" @click="batchPublish">
        <a-icon type="upload" />批量发布
      </a-button>
    </div>

    <!-- 文本粘贴区域 -->
    <div class="paste-section">
      <h3 class="section-title">📋 粘贴文章文本</h3>
      <p class="section-desc">将包含多篇文章的文本粘贴到下方，系统会自动提取标题和正文</p>
      <textarea 
        v-model="rawText" 
        class="paste-textarea" 
        placeholder="请粘贴包含多篇文章的文本"
      ></textarea>
      <a-button type="primary" @click="parseText">
        <a-icon type="magic" />智能提取
      </a-button>
    </div>

    <!-- 提取后的文章列表 -->
    <div class="article-list">
      <h3 class="section-title">📝 提取结果（{{ articles.length }} 篇）</h3>
      <div v-for="(article, index) in articles" :key="index" class="article-card">
        <div class="article-header">
          <span class="index">{{ index + 1 }}</span>
          <input 
            v-model="article.title" 
            class="title-input" 
            placeholder="请输入文章标题"
          />
        </div>
        <textarea 
          v-model="article.content" 
          class="content-textarea" 
          placeholder="请输入文章内容"
        ></textarea>
        <div class="article-footer">
          <a-button size="small" @click="removeArticle(index)">删除</a-button>
        </div>
      </div>
    </div>

    <div class="add-section">
      <a-button type="dashed" @click="addArticle">
        <a-icon type="plus" />添加文章
      </a-button>
    </div>
  </div>
</template>

<script>
import { reactive, ref } from 'vue'
import { batchPublishArticles } from '@/api/blog'

export default {
  name: 'BatchPublish',
  setup() {
    const rawText = ref('')
    const articles = reactive([])

    // 解析文本，提取文章
    const parseText = () => {
      if (!rawText.value.trim()) {
        alert('请先粘贴文本内容')
        return
      }

      articles.length = 0

      // 按换行分割文本
      const lines = rawText.value.split('\n')
      
      let currentArticle = null
      let buffer = ''

      for (let i = 0; i < lines.length; i++) {
        const line = lines[i]
        
        // 判断是否是新文章的开始（以数字开头，如 "1｜" 或 "1|"）
        const articleStartMatch = line.match(/^(\d+)[｜|]\s*标题：(.+)$/)
        
        if (articleStartMatch) {
          // 保存之前的文章
          if (currentArticle && buffer.trim()) {
            const content = extractContent(buffer)
            if (content) {
              currentArticle.content = content
              articles.push(currentArticle)
            }
          }

          // 开始新文章
          currentArticle = {
            title: articleStartMatch[2].trim(),
            content: ''
          }
          buffer = ''
        } else if (currentArticle) {
          // 累积内容
          buffer += (buffer ? '\n' : '') + line
        }
      }

      // 保存最后一篇文章
      if (currentArticle && buffer.trim()) {
        const content = extractContent(buffer)
        if (content) {
          currentArticle.content = content
          articles.push(currentArticle)
        }
      }

      if (articles.length === 0) {
        alert('未能识别到文章，请检查文本格式')
      } else {
        alert(`成功识别 ${articles.length} 篇文章！`)
      }
    }

    // 从内容中提取正文（去掉配图建议和标签）
    const extractContent = (text) => {
      let content = text.trim()
      
      // 去掉 "配图建议：" 后面的所有内容
      const picSuggestMatch = content.match(/(.+?)(?:配图建议：|$)/s)
      if (picSuggestMatch && picSuggestMatch[1]) {
        content = picSuggestMatch[1].trim()
      }

      // 去掉末尾的 #标签（多个标签连在一起的情况）
      // 匹配以 # 开头到行尾的内容
      content = content.replace(/#[\u4e00-\u9fa5a-zA-Z0-9\s]+$/, '')
      
      return content.trim()
    }

    const addArticle = () => {
      articles.push({ title: '', content: '' })
    }

    const removeArticle = (index) => {
      articles.splice(index, 1)
    }

    const batchPublish = async () => {
      const validArticles = articles.filter(a => a.title.trim() && a.content.trim())
      
      if (validArticles.length === 0) {
        alert('请至少填写一篇文章')
        return
      }

      try {
        const response = await batchPublishArticles(validArticles)
        if (response.success) {
          alert(`成功发布 ${response.result} 篇文章！`)
          articles.length = 0
          rawText.value = ''
        } else {
          alert('发布失败：' + response.message)
        }
      } catch (error) {
        console.error('批量发布失败', error)
        alert('发布失败，请稍后重试')
      }
    }

    return {
      rawText,
      articles,
      parseText,
      addArticle,
      removeArticle,
      batchPublish
    }
  }
}
</script>

<style scoped>
.batch-publish {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 12px;
  border-bottom: 1px solid #eee;
}

.title {
  font-size: 24px;
  font-weight: bold;
}

.paste-section {
  background: #fafafa;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 24px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.section-desc {
  font-size: 14px;
  color: #888;
  margin-bottom: 12px;
}

.paste-textarea {
  width: 100%;
  min-height: 250px;
  padding: 12px;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  font-size: 14px;
  line-height: 1.6;
  resize: vertical;
  margin-bottom: 12px;
}

.article-list {
  margin-bottom: 20px;
}

.article-card {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.article-header {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.index {
  width: 28px;
  height: 28px;
  background: #1890ff;
  color: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: bold;
  margin-right: 12px;
  flex-shrink: 0;
}

.title-input {
  flex: 1;
  border: none;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  outline: none;
  background: transparent;
}

.content-textarea {
  width: 100%;
  min-height: 120px;
  border: none;
  resize: vertical;
  font-size: 14px;
  line-height: 1.6;
  color: #666;
  outline: none;
  background: transparent;
}

.article-footer {
  text-align: right;
  padding-top: 12px;
  border-top: 1px dashed #eee;
}

.add-section {
  text-align: center;
  padding: 20px;
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
}
</style>