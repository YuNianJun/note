//导入请求工具文件
import request from '@/utils/request.js'

//笔记分类列表查询
export const categoryListService = () => {
    return request.get('/category/list');
}

//笔记分类添加
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
//笔记列表查询
export const noteListService = (params) => {
    return request.post('/category/page', { params: params });
}
//添加笔记
export const noteAddService = (noteModel)=>{
    return request.post('/note/save',noteModel)
}
//修改笔记
export const noteManageUpdateService = (noteModel) => {
    return request.post('/note/update',noteModel)
}

//笔记分享
export const noteShareService = (noteModel) => {
    return request.post('/note/shareNote',noteModel)
}
//访问分享笔记
export const noteShareVisitService = (id, token) => {
    return request.get(`/note/viewSharedNote/${id}?token=${token}`);
}
//笔记放入回收站
export const noteManageDeleteService = (ids) => {
    return request.post('/note/putRecycleBin', { ids });
}

//回收站删除笔记
export const recycleBinDeleteService = (ids) => {
    return request.post('/note/delete/ids',{ ids });
}
//回收站查询笔记
export const recycleBinListService = (params) => {
    return request.post('/note/page', { params: params });
}
//回收站笔记恢复
export const recycleBinRecoverService = (ids) => {
    return request.post('/note/recover', { ids });
}
//新增笔记评论
export const noteCommentService = (params) => {
    return request.post('/comment/save', params);
}
//笔记评论列表
export const noteCommentListService = (params) => {
    return request.post('/comment/page', params);
}
//笔记评论删除
export const noteCommentDeleteService = (ids) => {
    return request.post('/comment/delete/ids', { ids });
}
