import { mount } from '@vue/test-utils';
import { describe, expect, test, vi } from 'vitest';
import { vuetify } from 'tests/globals.ts';
import CustomListDialog from '@/components/custom-list-dialog/CustomListDialog.vue';
import { VCardTitle, VTextField } from 'vuetify/components';

vi.stubGlobal('visualViewport', new EventTarget());

const items = (new Array(10))
  .fill(0)
  .map((_, i) => ({
    id: i,
    name: i.toString(),
  }));

describe('CustomListDialog', () => {
  test('label', async () => {
    const wrapper = mount(CustomListDialog, {
      props: { label: 'Fuck' },
      slots: {
        default(props) {
          return h('button', { ...props.props });
        },
      },
      global: { plugins: [vuetify] },
    });

    await wrapper.find('button').trigger('click');

    expect(wrapper.findComponent(VCardTitle).text()).toBe('Fuck');
  });

  test('search', async () => {
    const wrapper = mount(CustomListDialog, {
      props: { items },
      slots: {
        default(props) {
          return h('button', { ...props.props });
        },
      },
      global: { plugins: [vuetify] },
    });

    await wrapper.find('button').trigger('click');

    expect(wrapper.findComponent(VTextField).exists()).toBeTruthy();
  });
});
