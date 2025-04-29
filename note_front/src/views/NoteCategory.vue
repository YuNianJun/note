<script setup>
import {
  Edit,
  Delete
} from '@element-plus/icons-vue'
import { ref } from 'vue'
const categorys = ref([
  {
    id: 1,
    name: "前端",
    user_id: 1,
    synopsis: "前端笔记简介"
  },
  {
    id: 2,
    name: "后端",
    user_id: 1,
    synopsis: "后端笔记简介"
  }
])
//声明笔记分类列表查询异步函数
import {categoryListService} from '@/api/note.js'
import {useUserInfoStore} from '@/stores/userInfo.js'
import {ElMessage} from "element-plus";
const userInfoStore = useUserInfoStore();
const categoryList = async() => {
  const userId = userInfoStore.info.id;
  let result = await categoryListService({ user_id: userId });
  if(result.code === 200){
    //成功获取
    categorys.value = result.data;
  }else{
    //获取失败
    ElMessage.error('获取失败')
  }
}
//调用
categoryList();
//控制添加分类弹窗
const dialogVisible = ref(false)

//添加分类数据模型
const categoryModel = ref({
  id: '',
  name: '',
  synopsis: ''
})
//添加分类表单校验
const rules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
  ],
  synopsis: [
    { required: true, message: '请输入分类简介', trigger: 'blur' },
  ]
};
//导入Element-Plus提示框组件

//导入categoryAddService函数
import {categoryAddService} from '@/api/note.js'
// 添加分类
const addCategory = async () => {
  let result = await categoryAddService(categoryModel.value);
  if(result.code === 200){
    // 成功添加
    ElMessage(result.message? result.message:'添加成功')
    // 隐藏弹窗
    dialogVisible.value = false
    // 再次访问后台接口，查询所有分类
    await categoryList();
  }else{
    // 添加失败
    ElMessage.error('添加失败')
  }
}
//定义变量控制弹窗标题
const title=ref('')
//修改分类回显
const updateCategoryEcho = (row) => {
  title.value = '修改分类';
  dialogVisible.value = true;
  // 将row中的数据赋值给categoryModel
  categoryModel.value.name = row.name;
  categoryModel.value.synopsis = row.synopsis;
  // 修改的时候必须传递分类的id，所以扩展一个id属性
  categoryModel.value.id = row.id;
};
//导入categoryUpdateServicee函数
import {categoryUpdateService} from '@/api/note.js'
//修改分类
const updateCategory=async ()=>{
  const requestData = {
    id: categoryModel.value.id,
    name: categoryModel.value.name,
    synopsis: categoryModel.value.synopsis
  };
  let result = await categoryUpdateService(requestData)
  if(result.code === 200){
    ElMessage.success(result.message? result.message:'修改成功')
    //隐藏弹窗
    dialogVisible.value=false
    //再次访问后台接口，查询所有分类
    categoryList();
  }else{
    //添加失败
    ElMessage.error('修改失败')
  }
}
//清空模型数据
const clearData = () => {
  categoryModel.value.name = '';
  categoryModel.value.synopsis = '';
};

//导入element的ElMessageBox提示框组件
import { ElMessageBox } from 'element-plus'
//导入categoryDeleteService函数
import {categoryDeleteService} from '@/api/note.js'
//删除分类
const deleteCategory = (row) => {
  ElMessageBox.confirm(
      '确认是否删除该分类信息？这将会永久删除该分类下的所有笔记，请谨慎操作！',
      '提示',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
      }
  )
      .then(async () => {
        //用户点击了确认
        let result = await categoryDeleteService(row.id)
        ElMessage.success(result.message?result.message:'删除成功')
        //再次调用getAllCategory，获取所有笔记分类
        categoryList();
      })
      .catch(() => {
        //用户点击了取消
        ElMessage({
          type: 'info',
          message: '取消删除',
        })
      })
}
</script>
<template>
  <el-card class="page-container">
    <template #header>
      <div class="header">
        <span>笔记分类</span>
        <div class="extra">
          <el-button type="primary" @click="dialogVisible = true;title='添加分类';clearData()">添加分类</el-button>
        </div>
      </div>
    </template>
    <el-table :data="categorys" style="width: 100%">
      <el-table-column label="序号" width="100" type="index"> </el-table-column>
      <el-table-column label="分类名称" prop="name"></el-table-column>
      <el-table-column label="分类简介" prop="synopsis"></el-table-column>
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button :icon="Edit" circle plain type="primary" @click="updateCategoryEcho(row)"></el-button>
          <el-button :icon="Delete" circle plain type="danger" @click="deleteCategory(row)"></el-button>
        </template>
      </el-table-column>
      <template #empty>
        <el-empty description="没有数据" />
      </template>
    </el-table>
    <!-- 添加分类弹窗 -->
    <el-dialog v-model="dialogVisible" :title="title" title="添加弹层" width="30%">
      <el-form :model="categoryModel" :rules="rules" label-width="100px" style="padding-right: 30px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="categoryModel.name" minlength="1" maxlength="10"></el-input>
        </el-form-item>
        <el-form-item label="分类简介" prop="categoryAlias">
          <el-input v-model="categoryModel.synopsis" minlength="1" maxlength="15"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
                <span class="dialog-footer">
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="title==='添加分类'? addCategory():updateCategory()"> 确认 </el-button>
                </span>
      </template>
    </el-dialog>
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
</style>