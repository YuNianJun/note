<script setup>
import {
  Edit,
  Delete,
  Share
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

//用户搜索时选中的公开状态
const status=ref('')

//笔记列表数据模型
const notes = ref([
  {
    id: 1,
    title: "springboot开发",
    tags: "java",
    content: "springboot,从入门到入坟",
    contentMd: "string",
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
    contentMd: "string",
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
import {categoryListService, noteShareService,noteShareVisitService} from '@/api/note.js'
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
import { noteListService } from '@/api/note.js'
// 修改后的获取笔记方法
const getnotes = async () => {
  try {
    const statusMap = { '已公开': 1, '私有': 0 };

    // 请求参数
    let params = {
      page: pageNum.value,
      size: pageSize.value,
      categoryId: categoryId.value || null,
      status: status.value ? statusMap[status.value] : null
    };

    let result = await noteListService(params);
    console.log('完整响应:', result);

    // 修改判断条件
    if (result.code === 200) {
      // 正确访问data字段
      const responseData = result.data;

      // 展平数据（注意访问data.records）
      notes.value = responseData.records.flatMap(category =>
          category.notes.map(note => ({
            ...note,
            content: note.content || '',
            categoryName: category.name,
            createTime: note.createTime || '无', // 处理null值
            updateTime: note.updateTime || '无'
          }))
      );

      // 更新分页数据（从data中获取）
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

import {Plus} from '@element-plus/icons-vue'
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
const uploadSuccess = (img) => {
  //img就是后台响应的数据，格式为：{code:状态码，message：提示信息，data: 图片的存储地址}
  noteModel.value.coverImg=img.data
  console.log(img.data);
}

//导入Element-Plus提示框组件
import { ElMessage } from 'element-plus'
//导入noteAddService函数
import {noteAddService} from '@/api/note.js'
//添加笔记
const addnote=async (status)=>{
  noteModel.value.status = status === '已公开' ? 1 : 0;
  //由于本地文件存储 这里测试使用固定的网络url 如果有网络服务器存储那就可以不需要这个
  noteModel.value.coverImg = 'https://ts1.cn.mm.bing.net/th/id/R-C.4bdc8f7f0e0201905fe400fb5156b7c7?rik=MVFo1SU7cYgFqg&riu=http%3a%2f%2fwww.spasvo.com%2fckfinder%2fuserfiles%2fimages%2f2020061536450116.jpg&ehk=r7Pp%2fX3wIOhP%2fcuW0ITLAHeD0sZPNatsyfpC3XWOM0s%3d&risl=&pid=ImgRaw&r=0'
  let result = await noteAddService(noteModel.value);
  if(result.code === 200) {
    //成功
    ElMessage.success(result.message? result.message:'添加成功')
    //再次调用getnotes,获取笔记
    getnotes()
    //隐藏抽屉
    visibleDrawer.value=false
  }else{
    //失败
    ElMessage.error('添加失败')
  }
}

//定义变量控制弹窗标题
const titles=ref('')

// 编辑笔记回显
const updateCategoryEcho = (row) => {
  titles.value = '编辑笔记';
  visibleDrawer.value = true;
  // 将row中的数据赋值给noteModel
  noteModel.value.title = row.title;
  noteModel.value.categoryId = row.categoryId;
  noteModel.value.coverImg = row.coverImg;
  noteModel.value.content = row.content;
  noteModel.value.tags = row.tags;
  noteModel.value.status = row.status; // 确保 status 是数字
  // 修改的时候必须传递分类的 id，所以扩展一个 id 属性
  noteModel.value.id = row.id;
};

//导入noteManageUpdateService函数
import {noteManageUpdateService} from '@/api/note.js'
// 编辑笔记
const updateManage = async (status) => {
  noteModel.value.status = status === '已公开' ? 1 : 0; // 根据传入的 status 设置值
  const requestData = {
    id: noteModel.value.id,
    title: noteModel.value.title,
    tags: noteModel.value.tags || '',
    content: noteModel.value.content || '',
    status: noteModel.value.status, // 使用设置后的 status
    categoryId: noteModel.value.categoryId  // 补充 categoryId 字段
  };
  console.log('Request Data:', requestData);
  let result = await noteManageUpdateService(requestData);
  if (result.code === 200) {
    // 成功
    ElMessage.success(result.message ? result.message : '编辑成功');
    // 隐藏弹窗
    visibleDrawer.value = false;
    // 刷新分类列表 再次调用getnotes,获取笔记
    getnotes();
  } else {
    // 失败
    ElMessage.error('编辑失败');
  }
};
//清空模型数据
const clearData = () => {
  visibleDrawer.value = ''
  noteModel.value.title = ''
  noteModel.value.categoryId = ''
  noteModel.value.coverImg = ''
  noteModel.value.content = ''
  noteModel.value.status = ''
}

//导入element的ElMessageBox提示框组件
import { ElMessageBox } from 'element-plus'
//导入noteManageDeleteService函数
import {noteManageDeleteService} from '@/api/note.js'
import router from "@/router";
import axios from "axios";
// 删除笔记
const deleteManage = (row) => {
  ElMessageBox.confirm(
      '确认是否将该笔记移入回收站？',
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
          await noteManageDeleteService([row.id]);
          ElMessage.success('笔记已移入回收站');
          // 重新获取笔记列表
          await getnotes();
        } catch (error) {
          ElMessage.error('移入回收站失败: ' + error.message);
        }
      })
      .catch(() => {
        // 用户点击了取消
        ElMessage({
          type: 'info',
          message: '取消移入回收站',
        })
      })
}
// 检查用户是否登录
if (!tokenStore.token) {
  router.push('/login')
}
const formatStatus = (row) => {
  return row.status === 1 ? '已公开' : '私有';
};
const markdownOption = {
  bold: true, // 粗体
  italic: true, // 斜体
  header: true, // 标题
  underline: true, // 下划线
  strikethrough: true, // 中划线
  mark: true, // 标记
  superscript: true, // 上角标
  subscript: true, // 下角标
  quote: true, // 引用
  ol: true, // 有序列表
  ul: true, // 无序列表
  link: true, // 链接
  imagelink: true, // 图片链接
  code: true, // code
  table: true, // 表格
  fullscreen: true, // 全屏编辑
  htmlcode: true, // 展示html源码
  help: true, // 帮助
}
// 图片添加回调
const handleImageAdd = (pos, file) => {
  // 构造表单数据
  const formData = new FormData()
  formData.append('file', file)

  // 调用上传接口（需自行实现）
  uploadImage(formData).then(res => {
    // 替换编辑器中的图片路径
    const url = res.data.url // 假设接口返回图片URL
    this.$refs.mdEditor.$img2Url(pos, url)
  }).catch(error => {
    console.error('图片上传失败:', error)
  })
}
const uploadImage = async (formData) => {
  return await axios.post('/api/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
      'Authorization': tokenStore.token
    }
  })
}
const shareLink = ref('');
const shareDialogVisible = ref(false);
const shareNote = async (row) => {
  try {
    console.log('Selected row:', row)
    const id = row.id;
    console.log('Note ID:', id);
    if (!id) {
      ElMessage.error('笔记ID无效');
      return;
    }
    const result = await noteShareService({id});
    if (result.code === 200) {
      shareLink.value = result.data;
      shareDialogVisible.value = true;
    } else {
      ElMessage.error(result.msg || '获取分享链接失败');
    }
  } catch (error) {
    console.error('获取分享链接失败:', error);
    ElMessage.error('获取分享链接失败');
  }
};

// 复制分享链接
const copyShareLink = () => {
  navigator.clipboard.writeText(shareLink.value)
      .then(() => {
        ElMessage.success('分享链接已复制到剪贴板');
      })
      .catch(err => {
        console.error('复制失败:', err);
        ElMessage.error('复制失败');
      });
};
//前端验证分享链接
const validateShareLink = async (link) => {
  try {
    const url = new URL(link);
    const id = url.pathname.split('/').pop();
    const token = url.searchParams.get('token');
    const result = await noteShareVisitService({ id, token });
    if (result.code === 200) {
      ElMessage.success('分享链接验证成功');
      // 这里可以进行其他操作，比如跳转到分享页面
    } else {
      ElMessage.error(result.msg || '分享链接验证失败');
    }
  } catch (error) {
    console.error('验证分享链接失败:', error);
    ElMessage.error('验证分享链接失败');
  }
};
</script>
<template>
  <el-card class="page-container">
    <template #header>
      <div class="header">
        <span>笔记管理</span>
        <div class="extra">
          <el-button type="primary" @click="visibleDrawer = true;titles='添加笔记';clearData()" >添加笔记</el-button>
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
      <el-form-item label="笔记标签：">
        <el-input v-model="noteModel.tags" placeholder="请输入标签"></el-input>
      </el-form-item>
      <el-form-item label="笔记状态：">
        <el-select v-model="status" placeholder="请选择">
          <el-option label="已公开" value="已公开"></el-option>
          <el-option label="私有" value="私有"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="getnotes()">搜索</el-button>
        <el-button @click="categoryId='';status='';getnotes()">重置</el-button>
      </el-form-item>
    </el-form>
    <!-- 笔记列表 -->
    <el-table :data="notes" style="width: 100%">
      <el-table-column label="笔记标题" width="200" prop="title"></el-table-column>
      <el-table-column label="分类" prop="categoryName"></el-table-column>
      <el-table-column label="标签" prop="tags"></el-table-column>
      <el-table-column label="创建时间" prop="createTime"></el-table-column>
      <el-table-column label="修改时间" prop="updateTime"></el-table-column>
      <el-table-column label="状态">
        <template #default="{row}">
          {{ row.status === 1 ? '已公开' : '私有' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button :icon="Edit" circle plain type="primary" @click="updateCategoryEcho(row)"></el-button>
          <el-button :icon="Delete" circle plain type="danger" @click="deleteManage(row)"></el-button>
          <el-button :icon="Share" circle plain type="success" @click="shareNote(row)"></el-button>
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
    <!-- 分享对话框 -->
    <el-dialog v-model="shareDialogVisible" title="分享笔记" width="30%">
      <div>
        <el-input v-model="shareLink" readonly></el-input>
        <el-button type="primary" @click="copyShareLink" style="margin-top: 10px;">复制链接</el-button>
        <el-button type="primary" @click="validateShareLink(shareLink)" style="margin-top: 10px;">验证链接</el-button>
      </div>
    </el-dialog>
    <!-- 抽屉 -->
    <el-drawer v-model="visibleDrawer" :title="titles" direction="rtl" size="50%">
      <!-- 添加笔记表单 -->
      <el-form :model="noteModel" label-width="100px" >
        <el-form-item label="笔记标题" >
          <el-input v-model="noteModel.title" placeholder="请输入标题"></el-input>
        </el-form-item>
        <el-form-item label="笔记分类">
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
        <el-form-item label="笔记标签">
          <el-input v-model="noteModel.tags" placeholder="请输入标签"></el-input>
        </el-form-item>
        <el-form-item label="笔记封面">
          <el-upload class="avatar-uploader" :auto-upload="true" :show-file-list="false"
                     action="/api/upload" name = 'file' :headers="{'Authorization':tokenStore.token}" :on-success="uploadSuccess">
<!--            点击上传由于这里练习项目没有网络服务器 数据是上传到本地的所以浏览器会拦截本地文件加载导致不能造成数据回显，但是目录下是有图片成功上传到的。-->
<!--            后续继续完成项目建议修改src中的值为模拟的固定网络图片url地址-->
<!--            <img v-if="noteModel.coverImg" :src="noteModel.coverImg" class="avatar" />-->
            <img v-if="noteModel.coverImg" :src="'https://ts1.cn.mm.bing.net/th/id/R-C.4bdc8f7f0e0201905fe400fb5156b7c7?rik=MVFo1SU7cYgFqg&riu=http%3a%2f%2fwww.spasvo.com%2fckfinder%2fuserfiles%2fimages%2f2020061536450116.jpg&ehk=r7Pp%2fX3wIOhP%2fcuW0ITLAHeD0sZPNatsyfpC3XWOM0s%3d&risl=&pid=ImgRaw&r=0'" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon">
              <Plus />
            </el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="笔记内容">
          <div class="editor-container">
            <mavon-editor
                v-model="noteModel.content"
                :subfield="true"
            :defaultOpen="'edit'"
            :toolbarsFlag="true"
            :navigation="true"
            style="height: 1080px"
            />
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="titles === '添加笔记' ? addnote('已公开') : updateManage('已公开')">公开</el-button>
          <el-button type="info" @click="titles === '添加笔记' ? addnote('私有') : updateManage('私有')">私有</el-button>
        </el-form-item>
      </el-form>
    </el-drawer>
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

//::v-deep .v-note-panel {
//  width: 50% !important;
//  min-width: 50% !important;
//  height: 100% !important;
//}

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
/* 分享对话框样式 */
.share-dialog-content {
  display: flex;
  flex-direction: column;
  gap: 20px; /* 设置间距 */
}

.share-dialog-content .el-input {
  margin-bottom: 10px; /* 设置输入框底部间距 */
}
</style>