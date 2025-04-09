import request from '@/utils/request'

// 注册接口
export const userRegisterService = (registerData) => {
    return request.post('/user/register', registerData)
}

// 登录接口
export const userLoginService = (loginData) => {
    return request.post('/user/login', loginData);
}

// 退出登录接口
export const userLogoutService = () => {
    return request.get('/user/logout')
}

//获取个人信息
export const userInfoGetService = () => {
    return request.get(`/user/Info`)
}

//修改个人信息
export const userInfoUpdateService = (userInfo)=>{
    return request.post('/user/update',userInfo)
}

//修改头像
export const userAvatarUpdateService=(avatarUrl)=>{
    let params = new URLSearchParams();
    params.append('avatarUrl',avatarUrl)
    return request.post('/user/updateAvatar',params)
}
//重置密码
export const userPasswordUpdateService = (params) => {
    return request.post('/user/updatePwd',params)
}

export const userPageService = () =>{
    return request.post('user/page')
}

export const userUpdateStatusService = (status) => {
    return request.post('/user/status',{status})
}
export const userDeleteService = (userId) => {
    return request.post('/user/delete',{userId})
}
export const userCreateService = (user) => {
    return request.post('/user/save',user)
}

