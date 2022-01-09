<template>
  <div class="jsondiffpatch" v-html="val">

  </div>
</template>

<script>
export default {
  name: "jsondiffpatch"
}
</script>

<script setup>
import { ref, defineProps, watch } from "vue"

const val = ref('')
const jsondiffpatch = window.jsondiffpatch
const instance = jsondiffpatch.create({
  objectHash: function (obj, index) {
    if (!obj) return obj
    if (typeof obj._id !== 'undefined') {
      return obj._id;
    }
    if (typeof obj.id !== 'undefined') {
      return obj.id;
    }
    if (typeof obj.name !== 'undefined') {
      return obj.name;
    }
    return '$$index:' + index;
  },
});

const props = defineProps({
  left: {
    type: [Object, Array, String, Number],
    required: true,
    default: () => {
      return {}
    }
  },
  right: {
    type: [Object, Array, String, Number],
    required: true,
    default: () => {
      return {}
    }
  },
})

const diff = () => {
  const left = props.left
  const right = props.right
  console.log({left, right})
  const delta = instance.diff(left, right);
  val.value = jsondiffpatch.formatters.html.format(delta, left)
  console.log(val.value, '--------')
}

watch(() => props.left, () => {
  diff()
})


</script>

<style scoped>

</style>