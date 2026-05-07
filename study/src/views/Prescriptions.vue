<template>
  <div class="prescriptions-container">
    <h2>{{ userStore.isAdmin ? '处方管理' : '我的处方' }}</h2>
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane :label="userStore.isAdmin ? '全部处方' : '我的处方'" name="my-prescriptions">
        <div class="my-prescriptions-card">
          <div class="filter-bar">
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
            <el-table-column label="操作" :width="userStore.isAdmin ? '280' : '180'" fixed="right">
              <template #default="scope">
                <el-button type="primary" size="small" @click="viewPrescription(scope.row)">查看</el-button>
                <el-button v-if="scope.row.status === 'approved'" type="success" size="small" @click="downloadPrescription(scope.row)">下载</el-button>
                <template v-if="userStore.isAdmin && scope.row.status === 'pending'">
                  <el-button type="success" size="small" @click="approvePrescription(scope.row)">通过</el-button>
                  <el-button type="danger" size="small" @click="rejectPrescription(scope.row)">拒绝</el-button>
                </template>
              </template>
            </el-table-column>
          </el-table>
          <Pagination
            v-model:page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            @change="loadPrescriptions"
          />
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
            <template v-if="userStore.isAdmin && selectedPrescription.status === 'pending'">
              <el-button type="success" @click="approvePrescription(selectedPrescription)">审核通过</el-button>
              <el-button type="danger" @click="rejectPrescription(selectedPrescription)">拒绝</el-button>
            </template>
          </div>
        </div>
        <div v-else class="empty-state">
          <el-empty description="请选择一个处方查看详情"></el-empty>
        </div>
      </el-tab-pane>
    </el-tabs>

  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPrescriptions, getPrescriptionById, updatePrescriptionStatus, type PrescriptionVO } from '@/api/prescription'
import { useUserStore } from '@/stores/user'
import Pagination from '@/components/Pagination.vue'

const userStore = useUserStore()

const activeTab = ref('my-prescriptions')
const prescriptionSearch = ref('')
const prescriptionStatus = ref('')
const selectedPrescription = ref<PrescriptionVO | null>(null)
const loading = ref(false)
const myPrescriptions = ref<PrescriptionVO[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

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
      status: prescriptionStatus.value || undefined,
      page: currentPage.value,
      size: pageSize.value
    })
    myPrescriptions.value = res.data?.records || res.data || []
    total.value = res.data?.total || 0
  } catch {
    // 错误提示已由全局拦截器处理
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
  } catch {
    // 错误提示已由全局拦截器处理
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

const approvePrescription = async (prescription: PrescriptionVO) => {
  try {
    await ElMessageBox.confirm(`确定要通过处方 ${prescription.prescriptionNo} 吗？`, '审核确认', {
      confirmButtonText: '通过',
      cancelButtonText: '取消',
      type: 'success'
    })
    await updatePrescriptionStatus(prescription.id, 'approved')
    ElMessage.success('审核通过')
    loadPrescriptions()
  } catch (error) {
    if (error === 'cancel') return
  }
}

const rejectPrescription = async (prescription: PrescriptionVO) => {
  try {
    await ElMessageBox.confirm(`确定要拒绝处方 ${prescription.prescriptionNo} 吗？`, '审核确认', {
      confirmButtonText: '拒绝',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await updatePrescriptionStatus(prescription.id, 'rejected')
    ElMessage.success('已拒绝')
    loadPrescriptions()
  } catch (error) {
    if (error === 'cancel') return
  }
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
</style>
