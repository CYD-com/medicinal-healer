<template>
  <!-- 药品管理页面容器 -->
  <div class="drugs-container">
    <h2>药品管理</h2>
    <!-- 标签页组件，用于切换不同功能模块 -->
    <el-tabs v-model="activeTab">
      <!-- 药品列表标签页 -->
      <el-tab-pane label="药品列表" name="drug-list">
        <div class="drug-list-card">
          <!-- 搜索栏：包含搜索输入框和筛选下拉框 -->
          <div class="search-bar">
            <el-input 
              v-model="drugSearch" 
              placeholder="搜索药品名称、规格" 
              prefix-icon="el-icon-search" 
              style="width: 300px; margin-right: 16px"
              @keyup.enter="handleSearch"
            ></el-input>
            <!-- 药品分类筛选 -->
            <el-select 
              v-model="drugCategory" 
              placeholder="选择分类" 
              style="width: 200px; margin-right: 16px"
              @change="handleSearch"
            >
              <el-option label="全部" value=""></el-option>
              <el-option 
                v-for="category in categories" 
                :key="category.id" 
                :label="category.name" 
                :value="category.name"
              ></el-option>
            </el-select>
            <!-- 药品类型筛选 -->
            <el-select 
              v-model="drugType" 
              placeholder="选择类型" 
              style="width: 150px"
              @change="handleSearch"
            >
              <el-option label="全部" value=""></el-option>
              <el-option label="西药" value="western"></el-option>
              <el-option label="中成药" value="chinese"></el-option>
            </el-select>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
          </div>
          <!-- 药品列表表格 -->
          <el-table 
            :data="drugs" 
            style="width: 100%; margin-top: 20px"
            v-loading="loading"
            element-loading-text="加载中..."
          >
            <el-table-column prop="drugName" label="药品名称" width="180"></el-table-column>
            <el-table-column prop="specification" label="规格" width="180"></el-table-column>
            <el-table-column prop="manufacturer" label="生产厂家" width="180"></el-table-column>
            <el-table-column prop="category" label="分类" width="150"></el-table-column>
            <el-table-column prop="type" label="类型" width="100">
              <template #default="scope">
                {{ scope.row.type === 'western' ? '西药' : '中成药' }}
              </template>
            </el-table-column>
            <el-table-column prop="price" label="价格" width="100">
              <template #default="scope">
                ¥{{ scope.row.price.toFixed(2) }}
              </template>
            </el-table-column>
            <el-table-column prop="stock" label="库存" width="100"></el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button type="primary" size="small" @click="viewDrugDetail(scope.row)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
      <!-- 药品详情标签页 -->
      <el-tab-pane label="药品详情" name="drug-detail">
        <!-- 药品详情卡片 -->
        <div class="drug-detail-card" v-if="selectedDrug">
          <div class="drug-header">
            <h3>{{ selectedDrug.drugName }}</h3>
            <!-- 药品基本信息描述列表 -->
            <div class="drug-basic-info">
              <el-descriptions :column="4">
                <el-descriptions-item label="通用名称">{{ selectedDrug.genericName }}</el-descriptions-item>
                <el-descriptions-item label="英文名称">{{ selectedDrug.englishName }}</el-descriptions-item>
                <el-descriptions-item label="规格">{{ selectedDrug.specification }}</el-descriptions-item>
                <el-descriptions-item label="生产厂家">{{ selectedDrug.manufacturer }}</el-descriptions-item>
                <el-descriptions-item label="批准文号">{{ selectedDrug.approvalNo }}</el-descriptions-item>
                <el-descriptions-item label="分类">{{ selectedDrug.category }}</el-descriptions-item>
                <el-descriptions-item label="剂型">{{ selectedDrug.form }}</el-descriptions-item>
                <el-descriptions-item label="价格">¥{{ selectedDrug.price.toFixed(2) }}</el-descriptions-item>
                <el-descriptions-item label="库存">{{ selectedDrug.stock }} {{ selectedDrug.unit }}</el-descriptions-item>
                <el-descriptions-item label="保质期">{{ selectedDrug.shelfLife }}</el-descriptions-item>
                <el-descriptions-item label="存储条件">{{ selectedDrug.storage }}</el-descriptions-item>
              </el-descriptions>
            </div>
          </div>
          <!-- 药品图片 -->
          <div class="drug-image">
            <img :src="selectedDrug.image" alt="药品图片" style="max-width: 200px; border-radius: 8px" />
          </div>
          <!-- 药品详细信息 -->
          <div class="drug-details">
            <h4>适应症</h4>
            <p>{{ selectedDrug.indications }}</p>
            <h4>禁忌症</h4>
            <p>{{ selectedDrug.contraindications }}</p>
            <h4>不良反应</h4>
            <p>{{ selectedDrug.adverseReactions }}</p>
            <h4>注意事项</h4>
            <p>{{ selectedDrug.precautions }}</p>
            <h4>药物相互作用</h4>
            <p>{{ selectedDrug.drugInteractions }}</p>
            <h4>用法用量</h4>
            <p>{{ selectedDrug.dosage }}</p>
          </div>
        </div>
        <!-- 空状态提示 -->
        <div v-else class="empty-state">
          <el-empty description="请选择一个药品查看详情"></el-empty>
        </div>
      </el-tab-pane>
      <!-- 库存管理标签页 -->
      <el-tab-pane label="库存管理" name="inventory">
        <div class="inventory-card">
          <!-- 库存概览 -->
          <div class="inventory-summary">
            <div class="summary-item">
              <el-icon class="summary-icon"><Goods /></el-icon>
              <div class="summary-content">
                <div class="summary-value">{{ inventorySummary.totalDrugs }}</div>
                <div class="summary-label">药品总数</div>
              </div>
            </div>
            <div class="summary-item">
              <el-icon class="summary-icon"><Warning /></el-icon>
              <div class="summary-content">
                <div class="summary-value">{{ inventorySummary.lowStock }}</div>
                <div class="summary-label">库存不足</div>
              </div>
            </div>
            <div class="summary-item">
              <el-icon class="summary-icon"><Close /></el-icon>
              <div class="summary-content">
                <div class="summary-value">{{ inventorySummary.outOfStock }}</div>
                <div class="summary-label">缺货</div>
              </div>
            </div>
            <div class="summary-item">
              <el-icon class="summary-icon"><Clock /></el-icon>
              <div class="summary-content">
                <div class="summary-value">{{ inventorySummary.expiringSoon }}</div>
                <div class="summary-label">临期药品</div>
              </div>
            </div>
          </div>
          <!-- 库存明细表格 -->
          <el-table :data="inventoryItems" style="width: 100%; margin-top: 20px">
            <el-table-column prop="drugName" label="药品名称" width="180"></el-table-column>
            <el-table-column prop="specification" label="规格" width="180"></el-table-column>
            <el-table-column prop="stock" label="库存" width="100"></el-table-column>
            <el-table-column prop="safetyStock" label="安全库存" width="100"></el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.status === 'normal' ? 'success' : scope.row.status === 'low' ? 'warning' : 'danger'">{{ scope.row.status === 'normal' ? '正常' : scope.row.status === 'low' ? '不足' : '缺货' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="expiryDate" label="有效期至" width="180"></el-table-column>
            <el-table-column prop="daysToExpiry" label="剩余天数" width="100"></el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
// 导入Vue 3组合式API和相关组件
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Goods, Warning, Close, Clock } from '@element-plus/icons-vue'
// 导入药品相关API
import { getDrugList, getDrugDetail, getInventorySummary, getInventoryList, getDrugCategories } from '@/api/drug'
import type { DrugItem, DrugCategory, InventoryItem, InventorySummary } from '@/types'

// 响应式状态管理
const activeTab = ref<string>('drug-list') // 当前激活的标签页
const drugSearch = ref<string>('') // 搜索关键词
const drugCategory = ref<string>('') // 药品分类筛选
const drugType = ref<string>('') // 药品类型筛选
const selectedDrug = ref<DrugItem | null>(null) // 当前选中的药品详情
const loading = ref<boolean>(false) // 加载状态
const categories = ref<DrugCategory[]>([]) // 药品分类列表

// 药品数据
const drugs = ref<DrugItem[]>([]) // 药品列表
const inventorySummary = ref<InventorySummary>({ // 库存概览
  totalDrugs: 0,
  lowStock: 0,
  outOfStock: 0,
  expiringSoon: 0
})
const inventoryItems = ref<InventoryItem[]>([]) // 库存列表

/**
 * 获取药品列表
 * @description 调用后端接口获取药品列表，支持搜索和筛选
 */
const fetchDrugList = async (): Promise<void> => {
  try {
    loading.value = true
    const query = {
      drugName: drugSearch.value,
      category: drugCategory.value,
      type: drugType.value,
      page: 1,
      size: 100 // 一次性获取较多数据，避免分页
    }
    const response: any = await getDrugList(query)
    drugs.value = response.data.records
  } catch {
    // 错误提示已由全局拦截器处理
  } finally {
    loading.value = false
  }
}

/**
 * 获取库存概览
 * @description 调用后端接口获取库存统计数据
 */
const fetchInventorySummary = async (): Promise<void> => {
  try {
    const response: any = await getInventorySummary()
    inventorySummary.value = response.data
  } catch {
    // 错误提示已由全局拦截器处理
  }
}

/**
 * 获取库存列表
 * @description 调用后端接口获取库存明细
 */
const fetchInventoryList = async (): Promise<void> => {
  try {
    const response: any = await getInventoryList(1, 100)
    inventoryList.value = response.data.records
  } catch {
    // 错误提示已由全局拦截器处理
  }
}

/**
 * 获取药品分类
 * @description 调用后端接口获取药品分类列表
 */
const fetchDrugCategories = async (): Promise<void> => {
  try {
    const response: any = await getDrugCategories()
    categories.value = response.data
  } catch {
    // 错误提示已由全局拦截器处理
  }
}

/**
 * 查看药品详情
 * @param {DrugItem} drug - 药品对象
 * @description 点击药品列表中的详情按钮时调用，显示药品详细信息
 */
const viewDrugDetail = async (drug: DrugItem): Promise<void> => {
  try {
    loading.value = true
    const response: any = await getDrugDetail(drug.id)
    selectedDrug.value = response.data
    activeTab.value = 'drug-detail'
  } catch {
    // 错误提示已由全局拦截器处理
  } finally {
    loading.value = false
  }
}

// 监听搜索和筛选条件变化
const handleSearch = (): void => {
  fetchDrugList()
}

// 组件加载时获取数据
onMounted(async (): Promise<void> => {
  // 并行请求所有数据
  await Promise.all([
    fetchDrugList(),
    fetchInventorySummary(),
    fetchInventoryList(),
    fetchDrugCategories()
  ])
})
</script>

<style scoped>
/* 主容器样式 */
.drugs-container {
  padding: 20px;
}

/* 标题样式 */
.drugs-container h2 {
  margin-bottom: 20px;
  color: #333;
}

/* 卡片样式 */
.drug-list-card,
.drug-detail-card,
.inventory-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 24px;
}

/* 搜索栏样式 */
.search-bar {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

/* 药品详情头部样式 */
.drug-header h3 {
  margin-bottom: 16px;
  color: #333;
  font-size: 20px;
  font-weight: bold;
}

/* 药品基本信息样式 */
.drug-basic-info {
  margin-bottom: 24px;
}

/* 药品图片样式 */
.drug-image {
  text-align: center;
  margin-bottom: 24px;
}

/* 药品详情内容样式 */
.drug-details h4 {
  margin-top: 20px;
  margin-bottom: 8px;
  color: #333;
  font-size: 16px;
  font-weight: bold;
}

.drug-details p {
  color: #666;
  line-height: 1.5;
  margin-bottom: 16px;
}

/* 库存概览样式 */
.inventory-summary {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

/* 概览项样式 */
.summary-item {
  background: #f5f7fa;
  padding: 20px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 16px;
}

/* 概览图标样式 */
.summary-icon {
  font-size: 32px;
  color: #409EFF;
}

/* 概览内容样式 */
.summary-content {
  flex: 1;
}

/* 概览数值样式 */
.summary-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

/* 概览标签样式 */
.summary-label {
  font-size: 14px;
  color: #666;
  margin-top: 4px;
}

/* 空状态样式 */
.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 400px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 响应式样式 */
@media (max-width: 768px) {
  .drug-list-card,
  .drug-detail-card,
  .inventory-card {
    padding: 16px;
  }
  
  .search-bar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .inventory-summary {
    grid-template-columns: 1fr;
  }
}
</style>
