<template>

  <el-dialog v-model="dialogFormVisible" title="Shipping address" width="500">
    <el-form :model="form">
      <el-form-item label="category" :label-width="formLabelWidth">
        <el-input v-model="form.category" autocomplete="off" />
      </el-form-item>
      <el-form-item label="style" :label-width="formLabelWidth">
        <el-input v-model="form.style" autocomplete="off" />
      </el-form-item>
        <el-form-item label="status" :label-width="formLabelWidth">
            <el-input v-model="form.status" autocomplete="off" />
        </el-form-item>
        <el-form-item label="money" :label-width="formLabelWidth">
            <el-input v-model="form.money" autocomplete="off" />
        </el-form-item>
        <el-form-item label="time" :label-width="formLabelWidth">
            <el-input v-model="form.time" autocomplete="off" />
        </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleCancel" >取消</el-button>
        <el-button type="primary" @click="handleConfirm ">
          确认
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from 'vue'
import type { EditDialogForm } from '@/types'

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void
  (e: 'save', value: EditDialogForm): void
}>()

const props = defineProps<{
  visible: boolean
  editData?: Partial<EditDialogForm>
}>()

const formLabelWidth = '140px'
const dialogFormVisible = ref<boolean>(props.visible)

const form = reactive<EditDialogForm>({
  id: 0,
  category: '',
  style: '',
  status: '',
  time: '',
  money: 0,
})

// 监听父组件传入的visible变化，同步对话框显示状态
watch(() => props.visible, (newVal: boolean) => {
  dialogFormVisible.value = newVal
})

// 监听父组件传入的editData变化，初始化表单数据
watch(
  () => props.editData, 
  (newVal) => {
    if (newVal && newVal.id !== undefined) { // 确保传入有效数据
      Object.assign(form, newVal) // 将父组件数据复制到表单
    }
  }, 
  { deep: true }
)

// 取消按钮：关闭对话框并通知父组件
const handleCancel = (): void => {
  dialogFormVisible.value = false
  emit('update:visible', false)
}

// 确认按钮：将编辑后的数据传递给父组件
const handleConfirm = (): void => {
  emit('save', { ...form }) // 传递表单数据
  dialogFormVisible.value = false
  emit('update:visible', false)
}
</script>