import { ref } from 'vue'
import { ElMessage, ElLoading } from 'element-plus'

export function useRequest() {
  const loading = ref(false)
  let loadingInstance: ReturnType<typeof ElLoading.service> | null = null

  // 显示加载中提示
  const showLoading = (text: string = '加载中...') => {
    loadingInstance = ElLoading.service({
      lock: true,
      text,
      background: 'rgba(0, 0, 0, 0.7)'
    })
    loading.value = true
  }

  // 隐藏加载中提示
  const hideLoading = () => {
    if (loadingInstance) {
      loadingInstance.close()
      loadingInstance = null
    }
    loading.value = false
  }

  // 显示错误提示
  const showError = (message: string) => {
    ElMessage.error(message)
  }

  // 显示成功提示
  const showSuccess = (message: string) => {
    ElMessage.success(message)
  }

  return {
    loading,
    showLoading,
    hideLoading,
    showError,
    showSuccess
  }
}