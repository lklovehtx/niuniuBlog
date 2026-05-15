<template>
  <div class="wheel-container">
    <!-- 顶部：日期选择（左右滑动） -->
    <div class="date-wheel">
      <div class="wheel-track">
        <div class="wheel" ref="topWheelRef" @mousedown="startDrag($event, 'top')" @touchstart="startDrag($event, 'top')" @wheel.prevent="handleWheel($event, 'top')">
          <div 
            class="wheel-item"
            :class="{ active: selectedDate === item }"
            v-for="item in dateList" 
            :key="item"
            @click.stop="selectItem('selectedDate', item)"
          >
            {{ item }}
          </div>
        </div>
      </div>
    </div>
    
    <!-- 内容展示区（插槽） -->
    <div class="content-display">
      <slot></slot>
    </div>
  </div>
</template>

<script>
export default {
  name: 'TechWheelSelector',
  data() {
    return {
      dateList: [],
      selectedDate: '',
      // 拖动状态
      dragging: null,
      startX: 0,
      startScrollLeft: 0
    }
  },
  mounted() {
    // 动态生成最近30天的日期
    this.generateDateList()
    
    // 默认选中今天
    const today = this.formatDate(new Date())
    const todayIndex = this.dateList.indexOf(today)
    this.selectedDate = todayIndex >= 0 ? today : this.dateList[0]

    // 添加全局事件监听
    document.addEventListener('mousemove', this.handleMouseMove)
    document.addEventListener('mouseup', this.handleMouseUp)
    document.addEventListener('touchmove', this.handleTouchMove, { passive: false })
    document.addEventListener('touchend', this.handleTouchEnd)

    // 默认选中项滚动到指示器位置
    this.$nextTick(() => {
      this.scrollToSelected('top')
    })
  },
  beforeDestroy() {
    // 移除全局事件监听
    document.removeEventListener('mousemove', this.handleMouseMove)
    document.removeEventListener('mouseup', this.handleMouseUp)
    document.removeEventListener('touchmove', this.handleTouchMove)
    document.removeEventListener('touchend', this.handleTouchEnd)
  },
  watch: {
    selectedDate() {
      this.emitFilterChange()
    }
  },
  methods: {
    // 生成最近30天的日期列表
    generateDateList() {
      const dates = []
      const today = new Date()
      for (let i = 29; i >= 0; i--) {
        const date = new Date(today)
        date.setDate(today.getDate() - i)
        dates.push(this.formatDate(date))
      }
      this.dateList = dates
    },
    // 格式化日期为 M/D 格式
    formatDate(date) {
      const month = date.getMonth() + 1
      const day = date.getDate()
      return `${month}/${day}`
    },
    // 滚动到选中的项目
    scrollToSelected(wheelName) {
      const wheelRef = this.$refs[wheelName + 'WheelRef']
      if (!wheelRef) return

      const list = this.dateList
      const selectedValue = this.selectedDate

      const index = list.indexOf(selectedValue)
      if (index >= 0) {
        const itemSize = 115
        wheelRef.scrollLeft = index * itemSize
      }
    },
    // 处理鼠标滚轮事件
    handleWheel(e, wheelName) {
      const wheelRef = this.$refs[wheelName + 'WheelRef']
      if (!wheelRef) return

      const delta = e.deltaX

      wheelRef.scrollLeft += delta * 2

      // 自动选中当前滚动位置对应的项
      this.autoSelectByScroll(wheelRef, wheelName)
    },
    // 根据滚动位置自动选中
    autoSelectByScroll(wheelRef) {
      const scrollPos = wheelRef.scrollLeft
      const itemSize = 115
      const index = Math.round(scrollPos / itemSize)

      const list = this.dateList
      const dataKey = 'selectedDate'

      const clampedIndex = Math.max(0, Math.min(index, list.length - 1))
      this[dataKey] = list[clampedIndex]
    },
    emitFilterChange() {
      this.$emit('filter-change', {
        date: this.selectedDate
      })
    },
    selectItem(key, value) {
      this[key] = value
      // 选中后滚动到对应位置
      const wheelMap = {
        selectedDate: 'top'
      }
      const wheelName = wheelMap[key]
      if (wheelName) {
        this.$nextTick(() => {
          this.scrollToSelected(wheelName)
        })
      }
    },
    startDrag(e, wheelName) {
      const wheelRef = this.$refs[wheelName + 'WheelRef']
      if (!wheelRef) return

      this.dragging = wheelName
      const touch = e.touches ? e.touches[0] : e
      this.startX = touch.clientX
      this.startScrollLeft = wheelRef.scrollLeft

      wheelRef.style.transition = 'none'
      wheelRef.style.cursor = 'grabbing'
      
      if (e.preventDefault) {
        e.preventDefault()
      }
    },
    handleMouseMove(e) {
      this.handleMove(e)
    },
    handleTouchMove(e) {
      this.handleMove(e)
    },
    handleMove(e) {
      if (!this.dragging) return

      const wheelRef = this.$refs[this.dragging + 'WheelRef']
      if (!wheelRef) return

      const touch = e.touches ? e.touches[0] : e
      const deltaX = touch.clientX - this.startX

      wheelRef.scrollLeft = this.startScrollLeft - deltaX

      if (e.preventDefault) {
        e.preventDefault()
      }
    },
    handleMouseUp() {
      this.endDrag()
    },
    handleTouchEnd() {
      this.endDrag()
    },
    endDrag() {
      if (!this.dragging) return

      const wheelRef = this.$refs[this.dragging + 'WheelRef']
      if (!wheelRef) {
        this.dragging = null
        return
      }

      wheelRef.style.cursor = 'grab'
      
      // 计算应该选中哪个选项
      const scrollPos = wheelRef.scrollLeft
      const itemSize = 115
      const index = Math.round(scrollPos / itemSize)

      const list = this.dateList
      const dataKey = 'selectedDate'

      const clampedIndex = Math.max(0, Math.min(index, list.length - 1))
      
      wheelRef.style.transition = 'scroll-behavior 0.3s ease'
      wheelRef.scrollLeft = clampedIndex * itemSize

      this[dataKey] = list[clampedIndex]
      this.dragging = null
    }
  }
}
</script>

<style scoped>
.wheel-container {
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 0;
}

.date-wheel {
  width: 100%;
  height: 50px;
  margin-bottom: 10px;
  position: relative;
}

.wheel-track {
  width: 100%;
  height: 100%;
  overflow: hidden;
  position: relative;
  border-radius: 8px;
  background: #f8f9fa;
  border: 1px solid #e9ecef;
}

.wheel {
  display: flex;
  overflow-x: auto;
  scrollbar-width: none;
  -ms-overflow-style: none;
  padding: 15px 20px;
  height: 100%;
  align-items: center;
  gap: 15px;
  cursor: grab;
  will-change: transform;
  user-select: none;
}

.wheel:active {
  cursor: grabbing;
}

.wheel::-webkit-scrollbar {
  display: none;
}

.wheel-item {
  min-width: 80px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6c757d;
  font-size: 14px;
  border-radius: 6px;
  background: white;
  border: 1px solid #dee2e6;
  transition: all 0.3s ease;
  user-select: none;
  cursor: pointer;
  flex-shrink: 0;
  min-height: 30px;
}

.wheel-item:hover {
  color: #495057;
  border-color: #adb5bd;
  background: #f8f9fa;
  transform: scale(1.05);
}

.wheel-item.active {
  color: #007bff;
  border-color: #007bff;
  background: #e7f3ff;
  transform: scale(1.1);
}

.content-display {
  width: 100%;
  background: white;
  border-radius: 8px;
  border: 1px solid #e9ecef;
  overflow: hidden;
}
</style>