/**
 * view组件按需加载
 * 按需导入
 * **/
import Vue from 'vue';
import {
  Button,
	Table,
	Scroll,
	Sider,
	Spin,
	Steps,
	Col,
	Card,
	Checkbox,
	CheckboxGroup,
	Menu,
	MenuGroup,
	MenuItem,
	Message,
	Modal,
	DropdownMenu,
	Submenu,
	DropdownItem,
	DatePicker,
	Dropdown,
	Divider,
	Form,
	FormItem,
	Radio,
	RadioGroup,
	Icon,
	Input,
	InputNumber,
	Select,
	Page,
	Row,
	Tag,
	Tooltip,
	Avatar,
	Option,
	Alert,
	Badge,
	Cascader,
	Tree,
	Upload,
	Poptip,
	TabPane,
	Tabs,
	Switch,
	Notice
} from 'view-design'
const Iview = {
	install: (vue) => {
		vue.component('Button', Button);
		vue.component('Table', Table);
		vue.component('Scroll', Scroll);
		vue.component('Sider', Sider);
		vue.component('Spin', Spin);
		vue.component('Steps', Steps);
		vue.component('Col', Col);
		vue.component('Card', Card);
		vue.component('Checkbox', Checkbox);
		vue.component('CheckboxGroup', CheckboxGroup);
		vue.component('Menu', Menu);
		vue.component('MenuGroup', MenuGroup);
		vue.component('MenuItem', MenuItem);
		vue.component('Message', Message);
		vue.component('Modal', Modal);
		vue.component('DropdownMenu', DropdownMenu);
		vue.component('DatePicker', DatePicker);
		vue.component('Dropdown', Dropdown);
		vue.component('Divider', Divider);
		vue.component('Form', Form);
		vue.component('FormItem', FormItem);
		vue.component('Radio', Radio);
		vue.component('RadioGroup', RadioGroup);
		vue.component('Icon', Icon);
		vue.component('Input', Input);
		vue.component('InputNumber', InputNumber);
		vue.component('Select', Select);
		vue.component('Submenu', Submenu);
		vue.component('DropdownItem', DropdownItem);
		vue.component('Row', Row);
		vue.component('Tag', Tag);
		vue.component('Tooltip', Tooltip);
		vue.component('Page', Page);
		vue.component('Avatar', Avatar);
		vue.component('Option', Option );
		vue.component('Alert', Alert);
		vue.component('Badge', Badge);
		vue.component('Cascader', Cascader);
		vue.component('Tree', Tree);
		vue.component('Upload', Upload);
		vue.component('Poptip', Poptip);
		vue.component('TabPane', TabPane);
		vue.component('Tabs', Tabs);
		vue.component('i-switch', Switch);
	}
}
Vue.prototype.$Message = Message;
Vue.prototype.$Modal = Modal;
Vue.prototype.$Notice = Notice;
export default Iview;