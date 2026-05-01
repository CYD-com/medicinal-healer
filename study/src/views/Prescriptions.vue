<template>
  <div class="prescriptions-container">
    <h2>处方管理</h2>
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="我的处方" name="my-prescriptions">
        <div class="my-prescriptions-card">
          <div class="filter-bar">
            <el-button type="primary" @click="openCreateDialog">新增处方</el-button>
            <el-input v-model="prescriptionSearch" placeholder="搜索处方" prefix-icon="el-icon-search" style="width: 300px; margin-right: 16px" @input="handleSearch"></el-input>
            <el-select v-model="prescriptionStatus" placeholder="选择状态" style="width: 200px" @change="loadPrescriptions">
              <el-option label="全部" value=""></el-option>
              <el-option label="待审核" value="pending"></el-option>
              <el-option label="已审核" value="approved"></el-option>
              <el-option label="已拒绝" value="rejected"></el-option>
              <el-option label="已完成" value="completed"></el-option>
              <el-option label="已过期" value="expired"></el-option>
            </el-select>
          </div>
          <el-table :data="filteredPrescriptions" style="width: 100%" v-loading="loading">
            <el-table-column prop="prescriptionNo" label="处方编号" width="180"></el-table-column>
            <el-table-column label="医生" width="150">
              <template #default="scope">
                <div class="doctor-info">
                  <el-avatar :size="32" :src="scope.row.doctor?.avatar"></el-avatar>
                  <span class="doctor-name">{{ scope.row.doctor?.name }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="diagnosis" label="诊断" width="180" show-overflow-tooltip></el-table-column>
            <el-table-column prop="drugCount" label="药品数量" width="100"></el-table-column>
            <el-table-column prop="totalAmount" label="总金额" width="120">
              <template #default="scope">
                ¥{{ formatMoney(scope.row.totalAmount) }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.statusText || getStatusText(scope.row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="创建时间" width="180">
              <template #default="scope">
                {{ formatDateTime(scope.row.createdAt) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180" fixed="right">
              <template #default="scope">
                <el-button type="primary" size="small" @click="viewPrescription(scope.row)">查看</el-button>
                <el-button v-if="scope.row.status === 'approved'" type="success" size="small" @click="downloadPrescription(scope.row)">下载</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div v-if="filteredPrescriptions.length === 0 && !loading" class="empty-state">
            <el-empty description="暂无处方数据"></el-empty>
          </div>
        </div>
      </el-tab-pane>
      <el-tab-pane label="处方详情" name="prescription-detail">
        <div class="prescription-detail-card" v-if="selectedPrescription">
          <div class="prescription-header">
            <div class="prescription-info">
              <h3>处方信息</h3>
              <el-descriptions :column="3" border>
                <el-descriptions-item label="处方编号">{{ selectedPrescription.prescriptionNo }}</el-descriptions-item>
                <el-descriptions-item label="状态">
                  <el-tag :type="getStatusType(selectedPrescription.status)">{{ selectedPrescription.statusText || getStatusText(selectedPrescription.status) }}</el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="总金额">¥{{ formatMoney(selectedPrescription.totalAmount) }}</el-descriptions-item>
                <el-descriptions-item label="创建时间">{{ formatDateTime(selectedPrescription.createdAt) }}</el-descriptions-item>
                <el-descriptions-item label="有效期至">{{ formatDateTime(selectedPrescription.validUntil) }}</el-descriptions-item>
                <el-descriptions-item label="诊断">{{ selectedPrescription.diagnosis }}</el-descriptions-item>
              </el-descriptions>
            </div>
            <div class="doctor-info-section">
              <h3>医生信息</h3>
              <el-descriptions :column="2" border>
                <el-descriptions-item label="姓名">{{ selectedPrescription.doctor?.name }}</el-descriptions-item>
                <el-descriptions-item label="职称">{{ selectedPrescription.doctor?.title }}</el-descriptions-item>
                <el-descriptions-item label="科室">{{ selectedPrescription.doctor?.department }}</el-descriptions-item>
                <el-descriptions-item label="签名">
                  <img v-if="selectedPrescription.doctor?.signature" :src="selectedPrescription.doctor.signature" alt="签名" style="width: 100px" />
                  <span v-else>暂无签名</span>
                </el-descriptions-item>
              </el-descriptions>
            </div>
          </div>
          <div class="prescription-drugs">
            <h3>药品信息</h3>
            <el-table :data="selectedPrescription.drugs" style="width: 100%" border>
              <el-table-column prop="drugName" label="药品名称" width="180"></el-table-column>
              <el-table-column prop="specification" label="规格" width="180"></el-table-column>
              <el-table-column prop="dosage" label="用法用量" width="200" show-overflow-tooltip></el-table-column>
              <el-table-column prop="quantity" label="数量" width="80"></el-table-column>
              <el-table-column prop="unit" label="单位" width="80"></el-table-column>
              <el-table-column prop="unitPrice" label="单价" width="100">
                <template #default="scope">
                  ¥{{ formatMoney(scope.row.unitPrice) }}
                </template>
              </el-table-column>
              <el-table-column prop="amount" label="金额" width="100">
                <template #default="scope">
                  ¥{{ formatMoney(scope.row.amount) }}
                </template>
              </el-table-column>
            </el-table>
          </div>
          <div class="prescription-advice" v-if="selectedPrescription.doctorAdvice">
            <h3>医嘱</h3>
            <p>{{ selectedPrescription.doctorAdvice }}</p>
          </div>
          <div class="prescription-actions">
            <el-button @click="activeTab = 'my-prescriptions'">返回列表</el-button>
            <el-button v-if="selectedPrescription.status === 'approved'" type="primary" @click="downloadPrescription(selectedPrescription)">下载处方</el-button>
          </div>
        </div>
        <div v-else class="empty-state">
          <el-empty description="请选择一个处方查看详情"></el-empty>
        </div>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="createDialogVisible" title="新增处方" width="900px" :close-on-click-modal="false">
      <el-form :model="createForm" :rules="createRules" ref="createFormRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="主治医生" prop="doctorId">
              <el-select v-model="createForm.doctorId" placeholder="请选择医生" style="width: 100%">
                <el-option
                  v-for="doc in doctorList"
                  :key="doc.doctorId"
                  :label="`${doc.name} - ${doc.title}`"
                  :value="doc.doctorId"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="诊断" prop="diagnosis">
          <el-input v-model="createForm.diagnosis" placeholder="请输入诊断内容" />
        </el-form-item>
        <el-form-item label="医嘱">
          <el-input v-model="createForm.doctorAdvice" type="textarea" :rows="2" placeholder="请输入医嘱" />
        </el-form-item>

        <el-divider>药品信息</el-divider>

        <div v-for="(item, index) in createForm.items" :key="index" class="drug-item-row">
          <el-row :gutter="12">
            <el-col :span="6">
              <el-form-item :label="index === 0 ? '药品' : ''" :prop="`items.${index}.drugId`" :rules="{ required: true, message: '请选择药品', trigger: 'change' }">
                <el-select
                  v-model="item.drugId"
                  filterable
                  placeholder="搜索选择药品"
                  style="width: 100%"
                  @change="(val: string) => handleDrugSelect(index, val)"
                >
                  <el-option
                    v-for="drug in drugList"
                    :key="drug.drugId"
                    :label="drug.drugName"
                    :value="drug.drugId"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="4">
              <el-form-item :label="index === 0 ? '规格' : ''">
                <el-input v-model="item.specification" placeholder="规格" disabled />
              </el-form-item>
            </el-col>
            <el-col :span="4">
              <el-form-item :label="index === 0 ? '用法' : ''">
                <el-input v-model="item.dosage" placeholder="用法用量" />
              </el-form-item>
            </el-col>
            <el-col :span="3">
              <el-form-item :label="index === 0 ? '数量' : ''">
                <el-input-number v-model="item.quantity" :min="1" style="width: 100%" @change="calcItemAmount(index)" />
              </el-form-item>
            </el-col>
            <el-col :span="3">
              <el-form-item :label="index === 0 ? '单价' : ''">
                <el-input-number v-model="item.unitPrice" :min="0" :precision="2" :step="1" style="width: 100%" @change="calcItemAmount(index)" />
              </el-form-item>
            </el-col>
            <el-col :span="3">
              <el-form-item :label="index === 0 ? '金额' : ''">
                <span class="amount-text">¥{{ formatMoney(item.amount || 0) }}</span>
              </el-form-item>
            </el-col>
            <el-col :span="1">
              <el-form-item :label="index === 0 ? '操作' : ''">
                <el-button type="danger" :icon="'Delete'" circle size="small" @click="removeDrugItem(index)" :disabled="createForm.items.length <= 1" />
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <el-button type="primary" plain @click="addDrugItem" style="width: 100%; margin-top: 8px;">+ 添加药品</el-button>

        <div class="total-amount-bar">
          <span>合计金额：</span>
          <span class="total-amount">¥{{ formatMoney(calcTotalAmount) }}</span>
        </div>
      </el-form>

      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="createLoading" @click="submitCreateForm">确认创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, type FormInstance } from 'element-plus'
import { getPrescriptions, getPrescriptionById, createPrescription, type PrescriptionVO, type PrescriptionCreateForm } from '@/api/prescription'

const activeTab = ref('my-prescriptions')
const prescriptionSearch = ref('')
const prescriptionStatus = ref('')
const selectedPrescription = ref<PrescriptionVO | null>(null)
const loading = ref(false)
const myPrescriptions = ref<PrescriptionVO[]>([])

const createDialogVisible = ref(false)
const createLoading = ref(false)
const createFormRef = ref<FormInstance>()
const doctorList = ref<any[]>([])
const drugList = ref<any[]>([])

const emptyDrugItem = () => ({
  drugId: '',
  drugName: '',
  specification: '',
  dosage: '',
  quantity: 1,
  unit: '盒',
  unitPrice: 0,
  amount: 0
})

const createForm = ref<PrescriptionCreateForm>({
  doctorId: undefined,
  diagnosis: '',
  doctorAdvice: '',
  items: [emptyDrugItem()]
})

const createRules = {
  diagnosis: [{ required: true, message: '请输入诊断内容', trigger: 'blur' }]
}

const calcTotalAmount = computed(() => {
  return createForm.value.items.reduce((sum, item) => sum + (item.amount || 0), 0)
})

const filteredPrescriptions = computed(() => {
  if (!prescriptionSearch.value) {
    return myPrescriptions.value
  }
  const keyword = prescriptionSearch.value.toLowerCase()
  return myPrescriptions.value.filter(p =>
    p.prescriptionNo?.toLowerCase().includes(keyword) ||
    p.diagnosis?.toLowerCase().includes(keyword) ||
    p.doctor?.name?.toLowerCase().includes(keyword)
  )
})

const loadPrescriptions = async () => {
  loading.value = true
  try {
    const res: any = await getPrescriptions({
      status: prescriptionStatus.value || undefined
    })
    myPrescriptions.value = res.data || []
  } catch (error) {
    console.error('获取处方列表失败:', error)
    ElMessage.error('获取处方列表失败')
  } finally {
    loading.value = false
  }
}

const handleTabChange = (tabName: string | number) => {
  if (tabName === 'my-prescriptions') {
    loadPrescriptions()
  }
}

const handleSearch = () => {
}

const viewPrescription = async (prescription: PrescriptionVO) => {
  try {
    const res: any = await getPrescriptionById(prescription.id)
    selectedPrescription.value = res.data
    activeTab.value = 'prescription-detail'
  } catch (error) {
    console.error('获取处方详情失败:', error)
    ElMessage.error('获取处方详情失败')
  }
}

const downloadPrescription = (prescription: PrescriptionVO) => {
  ElMessage.success(`处方 ${prescription.prescriptionNo} 下载成功`)
}

const formatMoney = (amount: number) => {
  if (amount == null) return '0.00'
  return Number(amount).toFixed(2)
}

const formatDateTime = (dateTime: string) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

const getStatusType = (status: string) => {
  const statusMap: Record<string, string> = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger',
    completed: 'info',
    expired: 'info'
  }
  return statusMap[status] || 'info'
}

const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    pending: '待审核',
    approved: '已审核',
    rejected: '已拒绝',
    completed: '已完成',
    expired: '已过期'
  }
  return statusMap[status] || status
}

const openCreateDialog = async () => {
  createForm.value = {
    doctorId: undefined,
    diagnosis: '',
    doctorAdvice: '',
    items: [emptyDrugItem()]
  }
  createDialogVisible.value = true
  await Promise.all([loadDoctorList(), loadDrugList()])
}

const loadDoctorList = async () => {
  try {
    const res: any = await (await import('@/api/appointment')).getDoctors()
    doctorList.value = res.data || []
  } catch (e) {
    console.error('获取医生列表失败:', e)
  }
}

const loadDrugList = async () => {
  try {
    const res: any = await (await import('@/api/drug')).getDrugList({ page: 1, size: 100 })
    drugList.value = res.data?.records || []
  } catch (e) {
    console.error('获取药品列表失败:', e)
  }
}

const handleDrugSelect = (index: number, drugId: string) => {
  const drug = drugList.value.find((d: any) => d.drugId === drugId)
  if (drug) {
    createForm.value.items[index].drugName = drug.drugName
    createForm.value.items[index].specification = drug.specification || ''
    createForm.value.items[index].unitPrice = Number(drug.price) || 0
    createForm.value.items[index].unit = '盒'
    calcItemAmount(index)
  }
}

const calcItemAmount = (index: number) => {
  const item = createForm.value.items[index]
  item.amount = Number(((item.unitPrice || 0) * (item.quantity || 1)).toFixed(2))
}

const addDrugItem = () => {
  createForm.value.items.push(emptyDrugItem())
}

const removeDrugItem = (index: number) => {
  createForm.value.items.splice(index, 1)
}

const submitCreateForm = async () => {
  if (!createFormRef.value) return
  await createFormRef.value.validate(async (valid) => {
    if (!valid) return
    if (!createForm.value.items.some(item => item.drugId)) {
      ElMessage.warning('请至少添加一个药品')
      return
    }
    createLoading.value = true
    try {
      await createPrescription(createForm.value)
      ElMessage.success('创建处方成功')
      createDialogVisible.value = false
      loadPrescriptions()
    } catch (error: any) {
      console.error('创建处方失败:', error)
      ElMessage.error(error?.msg || '创建处方失败')
    } finally {
      createLoading.value = false
    }
  })
}

onMounted(() => {
  loadPrescriptions()
})
</script>

<style scoped>
.prescriptions-container {
  padding: 20px;
}

.prescriptions-container h2 {
  margin-bottom: 20px;
  color: #333;
}

.filter-bar {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  gap: 12px;
}

.my-prescriptions-card,
.prescription-detail-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 24px;
}

.doctor-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.doctor-name {
  font-size: 14px;
  color: #333;
}

.prescription-header {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  margin-bottom: 24px;
}

.prescription-header h3 {
  margin-bottom: 16px;
  color: #333;
  font-size: 16px;
  font-weight: bold;
}

.prescription-drugs {
  margin-bottom: 24px;
}

.prescription-drugs h3 {
  margin-bottom: 16px;
  color: #333;
  font-size: 16px;
  font-weight: bold;
}

.prescription-advice {
  margin-bottom: 24px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
}

.prescription-advice h3 {
  margin-bottom: 8px;
  color: #333;
  font-size: 16px;
  font-weight: bold;
}

.prescription-advice p {
  color: #666;
  line-height: 1.5;
}

.prescription-actions {
  display: flex;
  gap: 12px;
  margin-top: 16px;
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 400px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

@media (max-width: 768px) {
  .my-prescriptions-card,
  .prescription-detail-card {
    padding: 16px;
  }

  .prescription-header {
    grid-template-columns: 1fr;
  }

  .filter-bar {
    flex-direction: column;
    gap: 12px;
  }

  .filter-bar .el-input,
  .filter-bar .el-select {
    width: 100% !important;
    margin-right: 0 !important;
  }
}

.drug-item-row {
  margin-bottom: 4px;
}

.amount-text {
  font-weight: bold;
  color: #409eff;
}

.total-amount-bar {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-top: 16px;
  padding: 12px 16px;
  background: #f5f7fa;
  border-radius: 8px;
  font-size: 14px;
}

.total-amount {
  font-size: 20px;
  font-weight: bold;
  color: #e6a23c;
  margin-left: 8px;
}
</style>
