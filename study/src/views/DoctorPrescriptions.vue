<template>
  <div class="doctor-prescriptions">
    <div class="page-header">
      <el-button type="primary" @click="showCreateDialog">
        <el-icon><Plus /></el-icon>
        开具处方
      </el-button>
    </div>

    <el-table :data="prescriptions" stripe border style="width: 100%" v-loading="loading">
      <el-table-column prop="prescriptionNo" label="处方编号" width="200" />
      <el-table-column prop="diagnosis" label="诊断" show-overflow-tooltip />
      <el-table-column prop="doctorAdvice" label="医嘱" show-overflow-tooltip />
      <el-table-column prop="drugCount" label="药品数" width="80" />
      <el-table-column label="总金额" width="100">
        <template #default="{ row }">
          ¥{{ row.totalAmount?.toFixed(2) || '0.00' }}
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'valid' ? 'success' : 'info'" size="small">
            {{ row.statusText }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="开具时间" width="180" />
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="showDetail(row)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!loading && prescriptions.length === 0" description="暂无处方记录" />

    <el-dialog v-model="createVisible" title="开具处方" width="700px" top="5vh">
      <el-form :model="form" label-width="100px" ref="formRef" :rules="rules">
        <el-form-item label="诊断结果" prop="diagnosis">
          <el-input v-model="form.diagnosis" type="textarea" :rows="2" placeholder="请输入诊断结果" />
        </el-form-item>
        <el-form-item label="医嘱">
          <el-input v-model="form.doctorAdvice" type="textarea" :rows="2" placeholder="请输入医嘱（可选）" />
        </el-form-item>
        <el-form-item label="药品列表" prop="items">
          <div class="drug-list">
            <div v-for="(item, index) in form.items" :key="index" class="drug-item">
              <el-input v-model="item.drugName" placeholder="药品名称" style="width: 160px" />
              <el-input v-model="item.specification" placeholder="规格" style="width: 100px" />
              <el-input v-model="item.dosage" placeholder="用量" style="width: 100px" />
              <el-input-number v-model="item.quantity" :min="1" :max="100" style="width: 100px" />
              <el-input v-model="item.unit" placeholder="单位" style="width: 70px" />
              <el-input-number v-model="item.unitPrice" :min="0" :precision="2" style="width: 100px" />
              <el-button type="danger" :icon="Delete" circle @click="removeDrug(index)" />
            </div>
            <el-button type="primary" link @click="addDrug" style="margin-top: 8px">
              <el-icon><Plus /></el-icon>
              添加药品
            </el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">提交处方</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="处方详情" width="600px">
      <div v-if="currentPrescription" class="prescription-detail">
        <div class="detail-row">
          <span class="label">处方编号：</span>
          <span>{{ currentPrescription.prescriptionNo }}</span>
        </div>
        <div class="detail-row">
          <span class="label">诊断结果：</span>
          <span>{{ currentPrescription.diagnosis }}</span>
        </div>
        <div class="detail-row" v-if="currentPrescription.doctorAdvice">
          <span class="label">医嘱：</span>
          <span>{{ currentPrescription.doctorAdvice }}</span>
        </div>
        <el-table :data="currentPrescription.drugs" border style="margin: 16px 0">
          <el-table-column prop="drugName" label="药品名称" />
          <el-table-column prop="specification" label="规格" />
          <el-table-column prop="dosage" label="用量" />
          <el-table-column prop="quantity" label="数量" width="80" />
          <el-table-column prop="unit" label="单位" width="70" />
          <el-table-column label="单价" width="90">
            <template #default="{ row }">¥{{ row.unitPrice?.toFixed(2) }}</template>
          </el-table-column>
        </el-table>
        <div class="total-amount">
          合计：<strong>¥{{ currentPrescription.totalAmount?.toFixed(2) || '0.00' }}</strong>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Plus, Delete } from '@element-plus/icons-vue'
import { ElMessage, type FormInstance } from 'element-plus'
import { getPrescriptions, createPrescription, type PrescriptionItemForm, type PrescriptionCreateForm } from '@/api/prescription'

const prescriptions = ref<any[]>([])
const loading = ref(false)
const createVisible = ref(false)
const detailVisible = ref(false)
const submitting = ref(false)
const currentPrescription = ref<any>(null)
const formRef = ref<FormInstance>()

const emptyDrug = (): PrescriptionItemForm => ({
  drugId: '',
  drugName: '',
  specification: '',
  dosage: '',
  quantity: 1,
  unit: '',
  unitPrice: 0
})

const form = ref<PrescriptionCreateForm>({
  diagnosis: '',
  doctorAdvice: '',
  items: [emptyDrug()]
})

const rules = {
  diagnosis: [{ required: true, message: '请输入诊断结果', trigger: 'blur' }],
  items: [{ required: true, message: '请添加至少一个药品', trigger: 'change' }]
}

const fetchPrescriptions = async () => {
  loading.value = true
  try {
    const res: any = await getPrescriptions()
    if (res.code === 200) {
      prescriptions.value = res.data
    }
  } catch (e) {
    console.error('获取处方列表失败', e)
  } finally {
    loading.value = false
  }
}

const showCreateDialog = () => {
  form.value = { diagnosis: '', doctorAdvice: '', items: [emptyDrug()] }
  createVisible.value = true
}

const addDrug = () => {
  form.value.items.push(emptyDrug())
}

const removeDrug = (index: number) => {
  if (form.value.items.length <= 1) {
    ElMessage.warning('至少保留一个药品')
    return
  }
  form.value.items.splice(index, 1)
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  if (form.value.items.length === 0) {
    ElMessage.warning('请添加至少一个药品')
    return
  }
  submitting.value = true
  try {
    await createPrescription(form.value)
    ElMessage.success('处方开具成功')
    createVisible.value = false
    fetchPrescriptions()
  } catch {
    // 错误提示已由全局拦截器处理
  } finally {
    submitting.value = false
  }
}

const showDetail = (row: any) => {
  currentPrescription.value = row
  detailVisible.value = true
}

onMounted(fetchPrescriptions)
</script>

<style scoped>
.doctor-prescriptions {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 20px;
}

.drug-list {
  width: 100%;
}

.drug-item {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
  align-items: center;
  flex-wrap: wrap;
}

.prescription-detail {
  line-height: 1.8;
}

.detail-row {
  margin-bottom: 8px;
}

.detail-row .label {
  color: #909399;
  margin-right: 8px;
}

.total-amount {
  text-align: right;
  font-size: 16px;
  color: #303133;
  margin-top: 12px;
}
</style>
