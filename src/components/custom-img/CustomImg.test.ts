import { mount } from '@vue/test-utils';
import { describe, expect, test } from 'vitest';
import { VSkeletonLoader } from 'vuetify/components';
import { vuetify } from 'tests/globals.ts';
import CustomImg from '@/components/custom-img/CustomImg.vue';

describe('CustomImg', () => {
  test('default props', () => {
    const wrapper = mount(CustomImg, { global: { plugins: [vuetify] } });
    expect(wrapper.text()).toBe('Нет изображения');
  });

  test('loading', async () => {
    const wrapper = mount(CustomImg, {
      props: {
        loading: true,
        src: 'test',
      },
      global: { plugins: [vuetify] },
    });

    expect(wrapper.findComponent(VSkeletonLoader).exists()).toBeTruthy();
  });
});
