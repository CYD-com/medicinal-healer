<template>
  <div class="pagination-container" v-if="total > 0">
    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="currentPageSize"
      :page-sizes="pageSizes"
      :total="total"
      :background="background"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  total: number
  page?: number
  pageSize?: number
  pageSizes?: number[]
  background?: boolean
}

interface Emits {
  (e: 'update:page', val: number): void
  (e: 'update:pageSize', val: number): void
  (e: 'change'): void
}

const props = withDefaults(defineProps<Props>(), {
  page: 1,
  pageSize: 10,
  pageSizes: () => [10, 20, 50],
  background: true
})

const emit = defineEmits<Emits>()

const currentPage = computed({
  get: () => props.page,
  set: (val) => emit('update:page', val)
})

const currentPageSize = computed({
  get: () => props.pageSize,
  set: (val) => emit('update:pageSize', val)
})

const handleSizeChange = (val: number) => {
  emit('update:pageSize', val)
  emit('update:page', 1)
  emit('change')
}

const handleCurrentChange = (val: number) => {
  emit('update:page', val)
  emit('change')
}
</script>

<style scoped>
.pagination-container {
  display: flex;
  justify-content: center;
  padding: 20px 0 8px;
}
</style>
