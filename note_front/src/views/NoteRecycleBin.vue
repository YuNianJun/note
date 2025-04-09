<template>
  <div>
    <!-- 笔记列表 -->
    <ul>
      <li v-for="note in notes" :key="note.id">
        {{ note.title }}
        <button @click="deleteNote(note.id)">删除</button>
        <button @click="recoverNote(note.id)">恢复</button>
        <input type="checkbox" v-model="selectedNotes" :value="note.id" />
      </li>
    </ul>
    <!-- 批量操作 -->
    <div>
      <button @click="deleteSelectedNotes">批量删除</button>
      <button @click="recoverSelectedNotes">批量恢复</button>
    </div>
  </div>
</template>

<script>import { ref, onMounted } from 'vue';
import { recycleBinListService, recycleBinDeleteService, recycleBinRecoverService } from '@/api/article.js';

export default {
  setup() {
    const notes = ref([]);
    const selectedNotes = ref([]);

    const fetchNotes = async () => {
      try {
        const response = await recycleBinListService();
        if (response.code === 200) {
          notes.value = response.data.records; // 提取 records 字段
        } else {
          console.error('获取回收站笔记失败', response.msg);
        }
      } catch (error) {
        console.error('获取回收站笔记失败', error);
      }
    };

    const deleteNote = async (id) => {
      try {
        await recycleBinDeleteService(id);
        fetchNotes();
      } catch (error) {
        console.error('删除笔记失败', error);
      }
    };

    const recoverNote = async (id) => {
      try {
        await recycleBinRecoverService([id]);
        fetchNotes();
      } catch (error) {
        console.error('恢复笔记失败', error);
      }
    };

    const deleteSelectedNotes = async () => {
      try {
        await recycleBinDeleteService(selectedNotes.value);
        fetchNotes();
        selectedNotes.value = []; // 清空选中的笔记
      } catch (error) {
        console.error('批量删除笔记失败', error);
      }
    };

    const recoverSelectedNotes = async () => {
      try {
        await recycleBinRecoverService(selectedNotes.value);
        fetchNotes();
        selectedNotes.value = []; // 清空选中的笔记
      } catch (error) {
        console.error('批量恢复笔记失败', error);
      }
    };

    onMounted(() => {
      fetchNotes();
    });

    return {
      notes,
      selectedNotes,
      deleteNote,
      recoverNote,
      deleteSelectedNotes,
      recoverSelectedNotes
    };
  }
};
</script>
