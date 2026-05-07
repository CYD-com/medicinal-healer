<template>
  <div class="doctor-prescriptions">
    <div class="search-bar">
      <el-input
        v-model="searchForm.prescriptionNo"
        placeholder="处方编号"
        clearable
        style="width: 200px; margin-right: 12px"
        @clear="handleSearch"
        @keyup.enter="handleSearch"
      />
      <el-select
        v-model="searchForm.status"
        placeholder="处方状态"
        clearable
        style="width: 150px; margin-right: 12px"
        @change="handleSearch"
      >
        <el-option label="有效" value="valid" />
        <el-option label="已过期" value="expired" />
        <el-option label="已作废" value="void" />
      </el-select>
      <el-button type="primary" @click="handleSearch">
        <el-icon><Search /></el-icon>
        查询
      </el-button>
      <el-button @click="handleReset">重置</el-button>
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
      <el-table-column prop="createdAt" label="开具时间" width="130" />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="showDetail(row)">查看</el-button>
          <el-popconfirm title="确定要删除这条处方吗？" @confirm="handleDelete(row)">
            <template #reference>
              <el-button type="danger" link>删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <Pagination
      v-model:page="currentPage"
      v-model:page-size="pageSize"
      :total="total"
      @change="fetchPrescriptions"
    />

    <el-empty v-if="!loading && prescriptions.length === 0" description="暂无处方记录" />

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
import { Search } from '@element-plus/icons-vue'
import { ElMessage, type FormInstance } from 'element-plus'
import { getPrescriptions, getPrescriptionById, deletePrescription, type PrescriptionVO } from '@/api/prescription'
import Pagination from '@/components/Pagination.vue'

const prescriptions = ref<any[]>([])
const loading = ref(false)
const detailVisible = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const currentPrescription = ref<any>(null)

const searchForm = ref({
  prescriptionNo: '',
  status: ''
})

const statusTagType = (status: string) => {
  const map: Record<string, string> = {
    valid: 'success',
    expired: 'warning',
    void: 'danger'
  }
  return (map[status] || 'info') as any
}

const statusLabel = (status: string) => {
  const map: Record<string, string> = {
    valid: '有效',
    expired: '已过期',
    void: '已作废'
  }
  return map[status] || status
}

const fetchPrescriptions = async () => {
  loading.value = true
  try {
    const res: any = await getPrescriptions({
      page: currentPage.value,
      size: pageSize.value,
      prescriptionNo: searchForm.value.prescriptionNo || undefined,
      status: searchForm.value.status || undefined
    })
    if (res.code === 200) {
      prescriptions.value = res.data?.records || res.data || []
      total.value = res.data?.total || 0
    }
  } catch (e) {
    console.error('获取处方列表失败', e)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchPrescriptions()
}

const handleReset = () => {
  searchForm.value = { prescriptionNo: '', status: '' }
  currentPage.value = 1
  fetchPrescriptions()
}

const handlePageChange = (page: number) => {
  currentPage.value = page
  fetchPrescriptions()
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  fetchPrescriptions()
}

const showDetail = async (row: any) => {
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

const handleDelete = async (row: any) => {
  try {
    await deletePrescription(row.id)
    ElMessage.success('删除成功')
    fetchPrescriptions()
  } catch {
    // 错误提示已由全局拦截器处理
  }
}

onMounted(fetchPrescriptions)
</script>

<style scoped>
.doctor-prescriptions {
  padding: 0;
}

.search-bar {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
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
