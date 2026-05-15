<template>
  <div class="article-editor">
    <div class="header">
      <div class="header-content">
        <h1 class="logo" @click="goHome">博客系统</h1>
        <div class="header-actions">
          <a-button @click="goBack">取消</a-button>
          <a-button @click="saveDraft" :loading="saving">保存草稿</a-button>
          <a-button type="primary" @click="publish" :loading="publishing"
            >发布</a-button
          >
        </div>
      </div>
    </div>

    <div class="editor-container">
      <div class="title-input">
        <a-input
          v-model="article.title"
          placeholder="请输入文章标题..."
          :maxLength="100"
          class="title-field"
        />
      </div>

      <div class="meta-inputs">
        <div class="meta-item">
          <span class="label">分类：</span>
          <a-select
            v-model="article.categoryId"
            placeholder="请选择分类"
            style="width: 200px"
          >
            <a-select-option
              v-for="cat in categories"
              :key="cat.id"
              :value="cat.id"
            >
              {{ cat.name }}
            </a-select-option>
          </a-select>
        </div>
        <div class="meta-item">
          <span class="label">标签：</span>
          <a-input
            v-model="article.tags"
            placeholder="多个标签用逗号分隔"
            style="width: 300px"
          />
        </div>
        <div class="meta-item">
          <span class="label">封面：</span>
          <a-upload
            :before-upload="handleCoverUpload"
            :show-upload-list="false"
            accept="image/*"
          >
            <a-button> <a-icon type="upload" />上传封面 </a-button>
          </a-upload>
          <img
            v-if="article.coverImage"
            :src="article.coverImage"
            class="cover-preview"
          />
        </div>
      </div>

      <div class="content-editor">
        <div class="editor-toolbar">
          <a-button-group>
            <a-button @click="execCommand('bold')" title="加粗">
              <a-icon type="bold" />
            </a-button>
            <a-button @click="execCommand('italic')" title="斜体">
              <a-icon type="italic" />
            </a-button>
            <a-button @click="execCommand('underline')" title="下划线">
              <a-icon type="underline" />
            </a-button>
          </a-button-group>
          <a-button-group>
            <a-button
              @click="execCommand('insertUnorderedList')"
              title="无序列表"
            >
              <a-icon type="unordered-list" />
            </a-button>
            <a-button
              @click="execCommand('insertOrderedList')"
              title="有序列表"
            >
              <a-icon type="ordered-list" />
            </a-button>
          </a-button-group>
          <a-button-group>
            <a-button @click="execCommand('justifyLeft')" title="左对齐">
              <a-icon type="align-left" />
            </a-button>
            <a-button @click="execCommand('justifyCenter')" title="居中对齐">
              <a-icon type="align-center" />
            </a-button>
            <a-button @click="execCommand('justifyRight')" title="右对齐">
              <a-icon type="align-right" />
            </a-button>
          </a-button-group>
          <a-button-group>
            <a-button @click="execCommand('createLink')" title="插入链接">
              <a-icon type="link" />
            </a-button>
            <a-button @click="execCommand('formatBlock')" title="引用">
              <a-icon type="block" />
            </a-button>
          </a-button-group>
          <a-button-group>
            <a-upload
              :before-upload="handleImageUpload"
              :show-upload-list="false"
              accept="image/*"
            >
              <a-button title="插入图片">
                <a-icon type="picture" />
              </a-button>
            </a-upload>
            <a-upload
              :before-upload="handleVideoUpload"
              :show-upload-list="false"
              accept="video/*"
            >
              <a-button title="插入视频">
                <a-icon type="video-camera" />
              </a-button>
            </a-upload>
            <a-upload
              :before-upload="handleAudioUpload"
              :show-upload-list="false"
              accept="audio/*"
            >
              <a-button title="插入音频">
                <a-icon type="audio" />
              </a-button>
            </a-upload>
          </a-button-group>
        </div>
        <div
          ref="editor"
          class="editor-content"
          contenteditable="true"
          @input="handleInput"
          @paste="handlePaste"
        ></div>
      </div>

      <div class="summary-input">
        <span class="label">摘要：</span>
        <a-textarea
          v-model="article.summary"
          placeholder="请输入文章摘要（可选）..."
          :rows="3"
          :maxLength="200"
        />
      </div>

      <a-modal v-model="videoDialogVisible" title="插入视频" @ok="insertVideo">
        <a-input v-model="videoUrl" placeholder="请输入视频URL或Embed代码" />
      </a-modal>

      <a-modal v-model="audioDialogVisible" title="插入音频" @ok="insertAudio">
        <a-input v-model="audioUrl" placeholder="请输入音频URL" />
      </a-modal>
    </div>
  </div>
</template>

<script>
import { mapGetters } from "vuex";
import { addArticle, updateArticle, getArticleDetail } from "@/api/blog";

export default {
  name: "ArticleEditor",
  data() {
    return {
      article: {
        id: null,
        title: "",
        content: "",
        summary: "",
        coverImage: "",
        categoryId: undefined,
        tags: "",
        status: "1",
      },
      categories: [
        { id: "cat001", name: "技术分享" },
        { id: "cat002", name: "生活随笔" },
        { id: "cat003", name: "读书笔记" },
        { id: "cat004", name: "职场成长" },
      ],
      saving: false,
      publishing: false,
      isEdit: false,
      videoDialogVisible: false,
      audioDialogVisible: false,
      videoUrl: "",
      audioUrl: "",
    };
  },
  computed: {
    ...mapGetters("user", ["userId", "username", "nickname", "avatar"]),
  },
  mounted() {
    const articleId = this.$route.params.id;
    if (articleId) {
      this.isEdit = true;
      this.loadArticle(articleId);
    }
  },
  methods: {
    async loadArticle(id) {
      try {
        const response = await getArticleDetail(id);
        if (response.success) {
          this.article = response.result;
          this.$nextTick(() => {
            this.$refs.editor.innerHTML = this.article.content || "";
          });
        } else {
          this.$message.error("加载文章失败");
        }
      } catch (error) {
        this.$message.error("加载文章失败");
      }
    },
    execCommand(command) {
      document.execCommand(command, false, null);
      this.$refs.editor.focus();
    },
    handleInput() {
      this.article.content = this.$refs.editor.innerHTML;
    },
    handlePaste(e) {
      e.preventDefault();
      const text = e.clipboardData.getData("text/plain");
      document.execCommand("insertText", false, text);
    },
    handleCoverUpload(file) {
      const reader = new FileReader();
      reader.onload = (e) => {
        this.article.coverImage = e.target.result;
      };
      reader.readAsDataURL(file);
      return false;
    },
    handleImageUpload(file) {
      const reader = new FileReader();
      reader.onload = (e) => {
        const img = `<img src="${e.target.result}" style="max-width: 100%; height: auto;" />`;
        this.insertHTML(img);
      };
      reader.readAsDataURL(file);
      return false;
    },
    handleVideoUpload(file) {
      const reader = new FileReader();
      reader.onload = (e) => {
        const video = `<video src="${e.target.result}" controls style="max-width: 100%; margin: 10px 0;">您的浏览器不支持视频播放</video>`;
        this.insertHTML(video);
      };
      reader.readAsDataURL(file);
      return false;
    },
    handleAudioUpload(file) {
      const reader = new FileReader();
      reader.onload = (e) => {
        const audio = `<audio src="${e.target.result}" controls style="width: 100%; margin: 10px 0;">您的浏览器不支持音频播放</audio>`;
        this.insertHTML(audio);
      };
      reader.readAsDataURL(file);
      return false;
    },
    showVideoDialog() {
      this.videoUrl = "";
      this.videoDialogVisible = true;
    },
    showAudioDialog() {
      this.audioUrl = "";
      this.audioDialogVisible = true;
    },
    insertVideo() {
      if (!this.videoUrl.trim()) {
        this.$message.error("请输入视频URL");
        return;
      }

      let videoHtml = "";

      // 如果是YouTube
      if (
        this.videoUrl.includes("youtube.com") ||
        this.videoUrl.includes("youtu.be")
      ) {
        const videoId = this.extractYouTubeId(this.videoUrl);
        if (videoId) {
          videoHtml = `<iframe width="560" height="315" src="https://www.youtube.com/embed/${videoId}" frameborder="0" allowfullscreen style="max-width: 100%;"></iframe>`;
        }
      }
      // 如果是Bilibili
      else if (this.videoUrl.includes("bilibili.com")) {
        videoHtml = `<iframe width="560" height="315" src="${this.videoUrl}" frameborder="0" allowfullscreen style="max-width: 100%;"></iframe>`;
      }
      // 其他视频URL，尝试作为video标签
      else {
        videoHtml = `<video src="${this.videoUrl}" controls style="max-width: 100%;"></video>`;
      }

      this.insertHTML(videoHtml);
      this.videoDialogVisible = false;
    },
    insertAudio() {
      if (!this.audioUrl.trim()) {
        this.$message.error("请输入音频URL");
        return;
      }

      const audioHtml = `<audio src="${this.audioUrl}" controls style="width: 100%; margin: 10px 0;"></audio>`;
      this.insertHTML(audioHtml);
      this.audioDialogVisible = false;
    },
    insertHTML(html) {
      this.$refs.editor.focus();
      document.execCommand("insertHTML", false, html);
    },
    extractYouTubeId(url) {
      const regExp =
        /^.*(youtu.be\/|v\/|u\/\w\/|embed\/|watch\?v=|&v=)([^#&?]*).*/;
      const match = url.match(regExp);
      return match && match[2].length === 11 ? match[2] : null;
    },
    async saveDraft() {
      if (!this.validate()) return;

      this.saving = true;
      try {
        // 检查内容大小
        const contentSize = new Blob([this.article.content]).size;
        if (contentSize > 4 * 1024 * 1024) {
          this.$message.error("草稿内容过大，请减少视频或图片的大小");
          this.saving = false;
          return;
        }

        this.article.status = "0";
        if (this.isEdit) {
          // 模拟更新文章
          this.$message.success("草稿保存成功");
        } else {
          // 模拟添加文章
          this.article.id = Date.now().toString();
          this.isEdit = true;
          this.$message.success("草稿保存成功");
        }
      } catch (error) {
        this.$message.error("保存失败");
      } finally {
        this.saving = false;
      }
    },
    async publish() {
      if (!this.validate()) return;

      this.publishing = true;
      try {
        // 检查内容大小
        const contentSize = new Blob([this.article.content]).size;
        if (contentSize > 4 * 1024 * 1024) {
          this.$message.error("文章内容过大，请减少视频或图片的大小");
          this.publishing = false;
          return;
        }

        this.article.status = "1";
        if (this.isEdit) {
          // 调用后端 API 更新文章
          const response = await updateArticle(this.article);
          if (response.success) {
            this.$message.success("发布成功");
            this.$router.push("/blog/home");
          } else {
            this.$message.error(response.message || "发布失败");
          }
        } else {
          // 调用后端 API 添加文章
          const response = await addArticle(this.article);
          if (response.success) {
            this.$message.success("发布成功");
            this.$router.push("/blog/home");
          } else {
            this.$message.error(response.message || "发布失败");
          }
        }
      } catch (error) {
        this.$message.error("发布失败");
      } finally {
        this.publishing = false;
      }
    },
    validate() {
      if (!this.article.title.trim()) {
        this.$message.error("请输入文章标题");
        return false;
      }
      if (!this.article.content.trim()) {
        this.$message.error("请输入文章内容");
        return false;
      }
      if (!this.article.categoryId) {
        this.$message.error("请选择分类");
        return false;
      }
      return true;
    },
    goBack() {
      this.$router.back();
    },
    goHome() {
      this.$router.push("/blog/home");
    },
  },
};
</script>

<style scoped>
.article-editor {
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
  max-width: 1200px;
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

.header-actions {
  display: flex;
  gap: 12px;
}

.editor-container {
  max-width: 1000px;
  margin: 20px auto;
  padding: 0 20px;
}

.title-input {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 16px;
}

.title-field {
  border: none;
  font-size: 24px;
  padding: 0;
}

.title-field:focus {
  box-shadow: none;
}

.meta-inputs {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.meta-item:last-child {
  margin-bottom: 0;
}

.meta-item .label {
  width: 60px;
  font-weight: 500;
}

.cover-preview {
  width: 100px;
  height: 60px;
  object-fit: cover;
  margin-left: 12px;
  border-radius: 4px;
}

.content-editor {
  background: white;
  border-radius: 8px;
  margin-bottom: 16px;
}

.editor-toolbar {
  padding: 12px 20px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  gap: 12px;
}

.editor-content {
  min-height: 400px;
  padding: 20px;
  outline: none;
  font-size: 16px;
  line-height: 1.8;
}

.summary-input {
  background: white;
  border-radius: 8px;
  padding: 20px;
}

.summary-input .label {
  display: block;
  font-weight: 500;
  margin-bottom: 8px;
}
</style>
