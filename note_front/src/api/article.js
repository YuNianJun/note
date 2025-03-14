//导入请求工具文件
import request from '@/utils/request.js'

//文章分类列表查询
export const categoryListService = () => {
    return request.get('/category/list');
}

//文章分类添加
export const categoryAddService = (categoryModel) => {
    return request.post('/category/save',categoryModel)
}
//删除分类
export const categoryDeleteService = (id) => {
    return request.post('/category/delete/'+id)
}
//修改分类
export const categoryUpdateService = (categoryModel) => {
    return request.post('/category/update',categoryModel)
}
//文章列表查询
export const articleListService = (params) => {
    return request.post('/category/page', { params: params });
}
//添加文章
export const articleAddService = (articleModel)=>{
    return request.post('/note/save',articleModel)
}
//修改文章
export const articleManageUpdateService = (articleModel) => {
    return request.post('/note/update',articleModel)
}

//删除文章
export const articleManageDeleteService = (id) => {
    return request.post('/note/delete/'+id)
}
