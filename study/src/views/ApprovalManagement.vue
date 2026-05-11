<template>
  <div class="approval-container">
    <div class="stats-bar">
      <div class="stat-card pending">
        <div class="stat-number">{{ pendingCount }}</div>
        <div class="stat-label">待审批</div>
      </div>
      <div class="stat-card approved">
        <div class="stat-number">{{ approvedCount }}</div>
        <div class="stat-label">已通过</div>
      </div>
      <div class="stat-card rejected">
        <div class="stat-number">{{ rejectedCount }}</div>
        <div class="stat-label">已拒绝</div>
      </div>
      <div class="stat-card total">
        <div class="stat-number">{{ totalCount }}</div>
        <div class="stat-label">总处方数</div>
      </div>
    </div>

    <div class="table-card">
      <div class="search-bar">
        <el-select
          v-model="filterStatus"
          placeholder="审批状态"
          clearable
          style="width: 150px; margin-right: 12px"
          @change="handleFilterChange"
        >
          <el-option label="待审核" value="pending" />
          <el-option label="已通过" value="approved" />
          <el-option label="已拒绝" value="rejected" />
          <el-option label="有效" value="valid" />
          <el-option label="已完成" value="completed" />
          <el-option label="已过期" value="expired" />
        </el-select>
        <el-input
          v-model="searchKeyword"
          placeholder="搜索处方编号/医生/诊断"
          clearable
          style="width: 260px; margin-right: 12px"
          @clear="handleSearch"
          @keyup.enter="handleSearch"
        />
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon>
          查询
        </el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>

      <el-table :data="filteredPrescriptions" stripe border v-loading="loading" style="width: 100%">
        <el-table-column prop="prescriptionNo" label="处方编号" width="190" />
        <el-table-column label="开方医生" width="140">
          <template #default="{ row }">
            <div class="doctor-cell">
              <el-avatar :size="28" :src="row.doctor?.avatar" v-if="row.doctor?.avatar" />
              <span>{{ row.doctor?.name || '-' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="科室" width="120">
          <template #default="{ row }">
            {{ row.doctor?.department || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="diagnosis" label="诊断" show-overflow-tooltip />
        <el-table-column prop="drugCount" label="药品数" width="80" align="center" />
        <el-table-column label="总金额" width="110" align="right">
          <template #default="{ row }">
            <span class="amount">¥{{ row.totalAmount?.toFixed(2) || '0.00' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small">
              {{ statusTextMap[row.status] || row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="开具时间" width="160" />
        <el-table-column label="操作" width="240" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="showDetail(row)">查看</el-button>
            <template v-if="row.status === 'pending'">
              <el-button type="success" link size="small" @click="handleApprove(row)">通过</el-button>
              <el-button type="danger" link size="small" @click="handleReject(row)">拒绝</el-button>
            </template>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <Pagination
        v-model:page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        @change="fetchPrescriptions"
      />

      <el-empty v-if="!loading && filteredPrescriptions.length === 0" description="暂无处方记录" />
    </div>

    <el-dialog v-model="detailVisible" title="处方详情" width="680px" destroy-on-close>
      <div v-if="currentPrescription" class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="处方编号">{{ currentPrescription.prescriptionNo }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="statusTagType(currentPrescription.status)" size="small">
              {{ statusTextMap[currentPrescription.status] || currentPrescription.status }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="开方医生">{{ currentPrescription.doctor?.name }}（{{ currentPrescription.doctor?.title }}）</el-descriptions-item>
          <el-descriptions-item label="科室">{{ currentPrescription.doctor?.department }}</el-descriptions-item>
          <el-descriptions-item label="诊断" :span="2">{{ currentPrescription.diagnosis }}</el-descriptions-item>
          <el-descriptions-item label="总金额">¥{{ currentPrescription.totalAmount?.toFixed(2) || '0.00' }}</el-descriptions-item>
          <el-descriptions-item label="开具时间">{{ currentPrescription.createdAt }}</el-descriptions-item>
        </el-descriptions>

        <div class="drug-section">
          <h4>药品明细</h4>
          <el-table :data="currentPrescription.drugs" border size="small" style="width: 100%">
            <el-table-column prop="drugName" label="药品名称" />
            <el-table-column prop="specification" label="规格" width="120" />
            <el-table-column prop="dosage" label="用法用量" width="160" show-overflow-tooltip />
            <el-table-column prop="quantity" label="数量" width="70" align="center" />
            <el-table-column prop="unit" label="单位" width="60" align="center" />
            <el-table-column label="单价" width="90" align="right">
              <template #default="{ row }">¥{{ row.unitPrice?.toFixed(2) }}</template>
            </el-table-column>
            <el-table-column label="金额" width="90" align="right">
              <template #default="{ row }">¥{{ row.amount?.toFixed(2) }}</template>
            </el-table-column>
          </el-table>
        </div>

        <div class="advice-section" v-if="currentPrescription.doctorAdvice">
          <h4>医嘱</h4>
          <p>{{ currentPrescription.doctorAdvice }}</p>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailVisible = false">关闭</el-button>
          <el-button type="danger" @click="handleDelete(currentPrescription!)">删除</el-button>
          <template v-if="currentPrescription?.status === 'pending'">
            <el-button type="success" @click="handleApprove(currentPrescription)">审核通过</el-button>
            <el-button type="danger" @click="handleReject(currentPrescription)">拒绝</el-button>
          </template>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import {
  getPrescriptions,
  getPrescriptionById,
  updatePrescriptionStatus,
  deletePrescription,
  type PrescriptionVO
} from '@/api/prescription'
import Pagination from '@/components/Pagination.vue'

const prescriptions = ref<PrescriptionVO[]>([])
const loading = ref(false)
const detailVisible = ref(false)
const currentPrescription = ref<PrescriptionVO | null>(null)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const filterStatus = ref('')
const searchKeyword = ref('')

const statusTextMap: Record<string, string> = {
  pending: '待审核',
  approved: '已通过',
  rejected: '已拒绝',
  valid: '有效',
  completed: '已完成',
  expired: '已过期'
}

const statusTagType = (status: string) => {
  const map: Record<string, string> = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger',
    valid: 'success',
    completed: 'info',
    expired: 'info'
  }
  return (map[status] || 'info') as any
}

const pendingCount = computed(() =>
  prescriptions.value.filter((p) => p.status === 'pending').length
)
const approvedCount = computed(() =>
  prescriptions.value.filter((p) => p.status === 'approved').length
)
const rejectedCount = computed(() =>
  prescriptions.value.filter((p) => p.status === 'rejected').length
)
const totalCount = computed(() => total.value)

const filteredPrescriptions = computed(() => {
  if (!searchKeyword.value) return prescriptions.value
  const kw = searchKeyword.value.toLowerCase()
  return prescriptions.value.filter(
    (p) =>
      p.prescriptionNo?.toLowerCase().includes(kw) ||
      p.doctor?.name?.toLowerCase().includes(kw) ||
      p.diagnosis?.toLowerCase().includes(kw)
  )
})

const fetchPrescriptions = async () => {
  loading.value = true
  try {
    const res: any = await getPrescriptions({
      status: filterStatus.value || undefined,
      page: currentPage.value,
      size: pageSize.value
    })
    if (res.code === 200) {
      prescriptions.value = res.data?.records || res.data || []
      total.value = res.data?.total || 0
    }
  } catch {
  } finally {
    loading.value = false
  }
}

const handleFilterChange = () => {
  currentPage.value = 1
  fetchPrescriptions()
}

const handleSearch = () => {
  currentPage.value = 1
  fetchPrescriptions()
}

const handleReset = () => {
  filterStatus.value = ''
  searchKeyword.value = ''
  currentPage.value = 1
  fetchPrescriptions()
}

const showDetail = async (row: PrescriptionVO) => {
  try {
    const res: any = await getPrescriptionById(row.id)
    if (res.code === 200) {
      currentPrescription.value = res.data
    } else {
      currentPrescription.value = row
    }
  } catch {
    currentPrescription.value = row
  }
  detailVisible.value = true
}

const handleApprove = async (row: PrescriptionVO | null) => {
  if (!row) return
  try {
    await ElMessageBox.confirm(
      `确定通过处方 ${row.prescriptionNo} 吗？`,
      '审批确认',
      { confirmButtonText: '通过', cancelButtonText: '取消', type: 'success' }
    )
    await updatePrescriptionStatus(row.id, 'approved')
    ElMessage.success('审批通过')
    detailVisible.value = false
    fetchPrescriptions()
  } catch (err) {
    if (err === 'cancel') return
  }
}

const handleReject = async (row: PrescriptionVO | null) => {
  if (!row) return
  try {
    await ElMessageBox.confirm(
      `确定拒绝处方 ${row.prescriptionNo} 吗？`,
      '审批确认',
      { confirmButtonText: '拒绝', cancelButtonText: '取消', type: 'warning' }
    )
    await updatePrescriptionStatus(row.id, 'rejected')
    ElMessage.success('已拒绝')
    detailVisible.value = false
    fetchPrescriptions()
  } catch (err) {
    if (err === 'cancel') return
  }
}

const handleDelete = async (row: PrescriptionVO) => {
  try {
    await ElMessageBox.confirm(
      `确定删除处方 ${row.prescriptionNo} 吗？此操作不可恢复。`,
      '删除确认',
      { confirmButtonText: '确定删除', cancelButtonText: '取消', type: 'error' }
    )
    await deletePrescription(row.id)
    ElMessage.success('删除成功')
    detailVisible.value = false
    fetchPrescriptions()
  } catch (err) {
    if (err === 'cancel') return
  }
}

onMounted(fetchPrescriptions)
</script>

<style scoped>
.approval-container {
  padding: 0;
}

.stats-bar {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.stat-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.stat-number {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 13px;
  color: #909399;
}

.stat-card.pending .stat-number { color: #e6a23c; }
.stat-card.approved .stat-number { color: #67c23a; }
.stat-card.rejected .stat-number { color: #f56c6c; }
.stat-card.total .stat-number { color: #409eff; }

.table-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.search-bar {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.doctor-cell {
  display: flex;
  align-items: center;
  gap: 6px;
}

.amount {
  color: #f56c6c;
  font-weight: 600;
}

.detail-content {
  line-height: 1.8;
}

.drug-section {
  margin-top: 20px;
}

.drug-section h4 {
  margin-bottom: 10px;
  font-size: 15px;
  color: #303133;
}

.advice-section {
  margin-top: 16px;
  padding: 12px 16px;
  background: #f5f7fa;
  border-radius: 6px;
}

.advice-section h4 {
  margin-bottom: 6px;
  font-size: 15px;
  color: #303133;
}

.advice-section p {
  margin: 0;
  color: #606266;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
</style>
