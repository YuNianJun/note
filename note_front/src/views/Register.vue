<template>
  <div>
    <h2>Register</h2>
    <form @submit.prevent="handleRegister">
      <input v-model="username" placeholder="Username" required>
      <input v-model="password" type="password" placeholder="Password" required>
      <button type="submit">Register</button>
    </form>
    <p v-if="error">{{ error }}</p>
    <router-link to="/login">Login</router-link>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

const username = ref('');
const password = ref('');
const error = ref('');
const router = useRouter();

const handleRegister = async () => {
  try {
    await axios.post('http://localhost:8080/api/auth/register', {
      username: username.value,
      password: password.value
    });
    await router.push('/login');
  } catch (err) {
    error.value = 'Registration failed';
  }
};
</script>