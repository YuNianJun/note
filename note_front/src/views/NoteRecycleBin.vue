<script setup>
import {
  RefreshRight,
  Delete
} from '@element-plus/icons-vue'

import {onMounted, ref} from 'vue'
import 'mavon-editor/dist/css/index.css' // 基础样式（必须）
import 'highlight.js/styles/github.css'
//笔记分类数据模型
const categorys = ref([
  {
    id: 1,
    categoryName: "前端",
    user_id: 1,
    synopsis: "前端笔记简介"
  },
  {
    id: 2,
    categoryName: "后端",
    user_id: 1,
    synopsis: "后端笔记简介"
  },
])

//用户搜索时选中的分类id
const categoryId=ref('')

//用户搜索时选中的发布状态
const state=ref('')

//笔记列表数据模型
const notes = ref([
  {
    id: 1,
    title: "springboot开发",
    tags: "java",
    content: "springboot,从入门到入坟",
    coverImg: "string",
    categoryId: 2,
    create_time: "2025-03-03 11:55:30",
    update_time: "2025-03-03 11:55:30",
    top: 0,
    status: 1
  },
  {
    id: 2,
    title: "vue开发",
    tags: "vue",
    content: "vue,从开始到跑路",
    coverImg: "string",
    categoryId: 1,
    create_time: "2025-03-03 11:55:30",
    update_time: "2025-03-03 11:55:30",
    top: 0,
    status: 1
  },

])

//分页条数据模型
const pageNum = ref(1)//当前页
const total = ref(20)//总条数
const pageSize = ref(3)//每页条数

//当每页条数发生了变化，调用此函数
const onSizeChange = (size) => {
  pageSize.value = size
  getnotes()

}
//当前页码发生变化，调用此函数
const onCurrentChange = (num) => {
  pageNum.value = num
  getnotes()
}
//笔记列表查询
import {categoryListService, recycleBinRecoverService} from '@/api/note.js'
const getcategoryList = async () => {
  try {
    let resultC = await categoryListService();
    categorys.value = resultC.data || [];
  } catch (error) {
    ElMessage.error('分类加载失败');
  }
};

// 初始化时先加载分类
onMounted(async () => {
  await getcategoryList(); // 确保分类加载完成
  await getnotes();     // 再加载笔记
});
getcategoryList();

//笔记列表查询
import { recycleBinListService } from '@/api/note.js'
// 修改后的获取笔记方法
const getnotes = async () => {
  try {
    const statusMap = { '已发布': 1, '草稿': 0 };

    // 请求参数
    const params = {
      current: pageNum.value, // 使用后端要求的参数名
      size: pageSize.value,
      categoryId: categoryId.value || null, // 直接使用 categoryId
      status: state.value ? statusMap[state.value] : null // 直接使用 state
    };

    const result = await recycleBinListService(params);
    console.log('完整响应:', result);

    if (result.code === 200) {
      const responseData = result.data;

      // 处理数据
      notes.value = responseData.records.map(note => ({
        id: note.id,
        title: note.title || '',
        tags: note.tags || '',
        content: note.content || '',
        coverImg: note.cover_img || '', // 使用 cover_img
        categoryId: note.categoryId || null,
        createTime: note.createTime || '无', // 处理null值
        updateTime: note.updateTime || '无',
        deleteTime: note.deleteTime || "无",
        status: note.status || 0,
        categoryName: note.categoryName || '无分类' // 如果后端没有返回分类名称，可以手动处理
      }));

      // 更新分页数据
      total.value = responseData.total;
      pageSize.value = responseData.size;
      pageNum.value = responseData.current;
    } else {
      throw new Error(result.msg || '请求失败');
    }
  } catch (error) {
    console.error('获取笔记失败:', error);
    ElMessage.error(`获取失败: ${error.message}`);
  }
};


getnotes();

//控制抽屉是否显示
const visibleDrawer = ref(false)
//添加表单数据模型
const noteModel = ref({
  title: '',
  categoryId: '',
  coverImg: '',
  content: '',
  tags: '',
  status: ''
});
//导入token
import { useTokenStore } from '@/stores/token.js'
const tokenStore = useTokenStore();
//上传图片成功回调

//导入Element-Plus提示框组件
import { ElMessage } from 'element-plus'

//清空回收站

//定义变量控制弹窗标题
const titles=ref('')



import {recycleBinDeleteService} from '@/api/note.js'
const clearRecycleBin = () => {
  ElMessageBox.confirm(
      '确认是否清空回收站？',
      '提示',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
      }
  )
      .then(async () => {
        // 用户点击了确认
        try {
          const ids = notes.value.map(note => note.id);
          await recycleBinDeleteService(ids);
          ElMessage.success('回收站已清空');
          // 重新获取笔记列表
          await getnotes();
        } catch (error) {
          ElMessage.error('清空回收站失败: ' + error.message);
        }
      })
      .catch(() => {
        // 用户点击了取消
        ElMessage({
          type: 'info',
          message: '取消清空回收站',
        });
      });
};
//清空模型数据
const clearData = () => {
  visibleDrawer.value = ''
  noteModel.value.title = ''
  noteModel.value.categoryId = ''
  noteModel.value.coverImg = ''
  noteModel.value.content = ''
  noteModel.value.state = ''
}

//导入element的ElMessageBox提示框组件
import { ElMessageBox } from 'element-plus'


import router from "@/router";
import axios from "axios";
// 删除笔记
const deleteManage = (row) => {
  ElMessageBox.confirm(
      '确认是否将该笔记彻底删除？',
      '提示',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
      }
  )
      .then(async () => {
        // 用户点击了确认
        try {
          await recycleBinDeleteService([row.id]);
          ElMessage.success('笔记已彻底删除');
          // 重新获取笔记列表
          await getnotes();
        } catch (error) {
          ElMessage.error('笔记删除失败: ' + error.message);
        }
      })
      .catch(() => {
        // 用户点击了取消
        ElMessage({
          type: 'info',
          message: '取消删除笔记',
        })
      })
}
// 检查用户是否登录
if (!tokenStore.token) {
  router.push('/login')
}
const formatStatus = (row) => {
  return row.status === 1 ? '已发布' : '草稿';
};
const recoverNote = async (id) => {
  try {
    await recycleBinRecoverService([id]);
    ElMessage.success('笔记恢复成功');
    // 重新获取笔记列表
    await getnotes();
  } catch (error) {
    ElMessage.error('笔记恢复失败: ' + error.message);
  }
};

</script>
<template>
  <el-card class="page-container">
    <template #header>
      <div class="header">
        <span>回收站</span>
        <div class="extra">
          <el-button type="primary" @click="clearRecycleBin">清空回收站</el-button>
        </div>
      </div>
    </template>
    <!-- 搜索表单 -->
    <el-form inline>
      <el-form-item label="笔记分类：">
        <el-select
            v-model="noteModel.categoryId"
            placeholder="请选择分类"
        >
          <el-option
              v-for="item in categorys"
              :key="item.id"
              :label="item.name"
              :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="发布状态：">
        <el-select v-model="state" placeholder="请选择">
          <el-option label="已发布" value="已发布"></el-option>
          <el-option label="草稿" value="草稿"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="getnotes()">搜索</el-button>
        <el-button @click="categoryId='';state='';getnotes()">重置</el-button>
      </el-form-item>
    </el-form>
    <!-- 笔记列表 -->
    <el-table :data="notes" style="width: 100%">
      <el-table-column label="笔记标题" width="300" prop="title"></el-table-column>
      <el-table-column label="分类" prop="categoryName"></el-table-column>
      <el-table-column label="标签" prop="tags"></el-table-column>
      <el-table-column label="创建时间" prop="createTime"></el-table-column>
      <el-table-column label="修改时间" prop="updateTime"></el-table-column>
      <el-table-column label="删除时间" prop="deleteTime"></el-table-column>
      <el-table-column label="状态">
        <template #default="{row}">
          {{ row.status === 1 ? '已发布' : '草稿' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button :icon="RefreshRight" circle plain type="primary" @click="recoverNote(row.id)"></el-button>
          <el-button :icon="Delete" circle plain type="danger" @click="deleteManage(row)"></el-button>
        </template>
      </el-table-column>
      <template #empty>
        <el-empty description="没有数据" />
      </template>
    </el-table>
    <!-- 分页条 -->
    <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :page-sizes="[3, 5 ,10, 15]"
                   layout="jumper, total, sizes, prev, pager, next" background :total="total" @size-change="onSizeChange"
                   @current-change="onCurrentChange" style="margin-top: 20px; justify-content: flex-end" />
    <!-- 抽屉 -->

  </el-card>
</template>
<style lang="scss" scoped>
.page-container {
  min-height: 100%;
  box-sizing: border-box;

  .header {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
}
/* 抽屉样式 */
.avatar-uploader {
  :deep() {
    .avatar {
      width: 178px;
      height: 178px;
      display: block;
    }

    .el-upload {
      border: 1px dashed var(--el-border-color);
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      transition: var(--el-transition-duration-fast);
    }

    .el-upload:hover {
      border-color: var(--el-color-primary);
    }

    .el-icon.avatar-uploader-icon {
      font-size: 28px;
      color: #8c939d;
      width: 178px;
      height: 178px;
      text-align: center;
    }
  }
}

/* 容器样式 */
.editor-container {
  height: 100% !important; /* 关键修改 */
  display: flex;
  flex-direction: column;
}
/* 强制编辑器填满容器 */
::v-deep .v-note-wrapper {
  flex: 1;
  min-height: 0 !important; /* 允许内容压缩 */
  display: flex !important;
  flex-direction: column !important;
}

::v-deep .v-note-panel {
  flex: 1;
  min-height: 0 !important;
}

/* 移除默认内边距 */
::v-deep .v-note-edit,
::v-deep .v-note-preview {
  padding: 0 !important;
}
/* 强制分栏 */
::v-deep .v-note-wrapper.split-pane {
  flex: 1;
  height: 100% !important;
}

/* 编辑区样式 */
::v-deep .v-note-edit {
  padding-right: 10px;
  border-right: 1px solid #eee;
}

/* 预览区样式 */
::v-deep .v-note-preview {
  padding-left: 10px;
  overflow-y: auto;
}
@media (max-width: 768px) {
  ::v-deep .v-note-wrapper.split-pane {
    flex-direction: column !important;
  }
  ::v-deep .v-note-panel {
    width: 100% !important;
    height: 50vh !important;
  }
}
</style>