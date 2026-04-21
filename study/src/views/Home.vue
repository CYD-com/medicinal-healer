<template>
  <div class="home-page">
    <div class="table-header">
      <h3>记录列表</h3>
      <el-button type="primary" @click="handleAdd">添加记录</el-button>
      <el-button type="primary" @click="handleInit">初始化测试数据</el-button>
    </div>

    <div class="search-form">
      <el-form :model="searchForm" inline>
        <el-form-item label="分类">
          <el-input v-model="searchForm.category" placeholder="请输入分类" clearable />
        </el-form-item>
        <el-form-item label="类型">
          <el-input v-model="searchForm.type" placeholder="请输入类型" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-input v-model="searchForm.status" placeholder="请输入状态" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table :data="tableData" border stripe>
      <el-table-column type="index" label="序号" width="80" :index="indexMethod" />
      <el-table-column prop="category" label="分类" />
      <el-table-column prop="type" label="类型" />
      <el-table-column prop="status" label="状态" />
      <el-table-column prop="amount" label="金额" />
      <el-table-column prop="time" label="时间" width="180" />
      <el-table-column label="操作" width="120">
        <template #default="scope">
          <el-button type="text" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button type="text" danger @click="handleDelete(scope.row.id)"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="page"
      v-model:page-size="size"
      :total="total"
      layout="total, prev, pager, next, jumper"
      @size-change="getList"
      @current-change="getList"
      style="margin-top: 20px; text-align: right"
    />
  </div>

  <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑记录' : '添加记录'">
    <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
      <el-form-item label="分类" prop="category">
        <el-input v-model="form.category" />
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-input v-model="form.type" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-input v-model="form.status" />
      </el-form-item>
      <el-form-item label="金额" prop="amount">
        <el-input v-model.number="form.amount" />
      </el-form-item>
      <el-form-item label="时间" prop="time">
        <el-date-picker
          v-model="form.time"
          type="date"
          value-format="YYYY-MM-DD"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确认</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import {
  getRecordList,
  initRecordData,
  addRecord,
  deleteRecord,
  updateRecord,
} from "@/api/record";

const tableData = ref([]);
const page = ref(1);
const size = ref(10);
const total = ref(0);
const dialogVisible = ref(false);
const isEdit = ref(false);
const editId = ref(null);
const formRef = ref(null);
const form = ref({
  category: "",
  type: "",
  status: "",
  amount: "",
  time: "",
});
const searchForm = ref({
  category: "",
  type: "",
  status: "",
});
const rules = ref({
  category: [{ required: true, message: "请输入分类", trigger: "blur" }],
  type: [{ required: true, message: "请输入类型", trigger: "blur" }],
  status: [{ required: true, message: "请输入状态", trigger: "blur" }],
  amount: [{ required: true, message: "请输入金额", trigger: "blur" }],
  time: [{ required: true, message: "请输入时间", trigger: "blur" }],
});

// 计算序号
const indexMethod = (index) => {
  return (page.value - 1) * size.value + index + 1;
};

// 获取列表
const getList = async () => {
  const params = {
    page: page.value,
    size: size.value,
    ...searchForm.value,
  };
  const res = await getRecordList(params);
  if (res.code === 200) {
    tableData.value = res.data.records || [];
    total.value = res.data.total || 0;
  }
};

// 搜索
const handleSearch = () => {
  page.value = 1;
  getList();
};

// 重置搜索
const handleReset = () => {
  searchForm.value = {
    category: "",
    type: "",
    status: "",
  };
  page.value = 1;
  getList();
};

// 初始化数据
const handleInit = async () => {
  const res = await initRecordData();
  ElMessage.success(res.msg);
  getList();
};

const handleAdd = () => {
  isEdit.value = false;
  editId.value = null;
  form.value = {
    category: "",
    type: "",
    status: "",
    amount: "",
    time: "",
  };
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  try {
    await formRef.value.validate();
    if (isEdit.value) {
      await updateRecord(editId.value, form.value);
      ElMessage.success("编辑成功");
    } else {
      await addRecord(form.value);
      ElMessage.success("添加成功");
    }
    dialogVisible.value = false;
    getList();
  } catch (error) {
    if (error !== false) {
      ElMessage.error(error.message || "操作失败");
    }
  }
};

const handleCancel = () => {
  formRef.value?.resetFields();
};

// 编辑
const handleEdit = (row) => {
  isEdit.value = true;
  editId.value = row.id;
  form.value = {
    category: row.category,
    type: row.type,
    status: row.status,
    amount: row.amount,
    time: row.time,
  };
  dialogVisible.value = true;
};

// 删除
const handleDelete = async (id) => {
  try {
    const res = await deleteRecord(id);
    ElMessage.success("删除成功");
    getList();
  } catch (error) {
    ElMessage.error(error.message || "删除失败");
  }
};

onMounted(() => {
  getList();
});
</script>

<style scoped>
.home-page {
  padding: 20px;
}
.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.search-form {
  background: #f5f7fa;
  padding: 20px;
  border-radius: 4px;
  margin-bottom: 20px;
}
</style>
