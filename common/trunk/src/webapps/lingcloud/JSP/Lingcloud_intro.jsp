<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<br /><br />
<% if (loc.getLanguage().equals("zh")) { %>
<b>凌云（LingCloud）</b>是中国科学院计算技术研究所分布式与云计算研究团队开发的一套云计算系统软件。凌云系统支持云计算环境中物理资源与虚拟资源的统一管理，支持虚拟机群租赁、高性能计算、大规模数据处理、海量存储等多种应用模式的接入。凌云系统适合于构建面向政府、企业、学校与科研机构的私有云，也适合于管理面向数据中心的公有云。<br />
凌云系统已经在国内若干家科研单位生产性使用。为促进中国云计算产业的发展，加强学术界与产业界的交流，凌云开发团队已决定将凌云系统开源。凌云系统主体采用Apache License 2.0授权，诚挚欢迎有志于云计算的团队或个人参与凌云开发，让我们共同为中国云计算事业的发展贡献力量。<br />
<h3>凌云系统主要功能</h3>
&nbsp;&nbsp;●&nbsp;<b>资源分区</b>：以分区为单位组织和管理物理主机资源，支持多种应用模式或资源使用方式<br />
&nbsp;&nbsp;●&nbsp;<b>资源租赁</b>：以机群为单位租赁虚拟机与物理机，支持资源按需提供<br />
&nbsp;&nbsp;●&nbsp;<b>应用封装</b>：基于web页面制作虚拟电器（虚拟机映像模板），支持应用快速部署<br />
&nbsp;&nbsp;&nbsp;&nbsp;更多特性有待发布<br />
<h3>凌云系统体系结构</h3>
<img alt="LingCloud Architecture" src="<%=basePath%>/images/lingcloud-arch-zh.png" height="344px" width="604px"><br /><br />
<%} else{ %>
<b>LingCloud</b> is a suite of cloud computing system software developed by the Research Group of Distributed and Cloud Computing, Institute of Computing Technology, Chinese Academy of Sciences. LingCloud provides a resource single leasing point system for consolidated leasing physical and virtual machines, and supports various heterogeneous application modes including high performance computing, large scale data processing, massive data storage, etc. on shared infrastructure. LingCloud can help to build private clouds for governments, enterprises, schools and research institutes, and it is also suitable to construct public clouds for managing data centers.<br />
LingCloud has been adopted in some affiliations of China. In order to promote the development of cloud computing industry in China and strengthen the communication between academia and industry, the LingCloud development team decides to make LingCloud open-source. The main part of LingCloud is licensed under Apache License 2.0. We welcome any organizations or individuals dedicated to cloud computing to get involved in the development of LingCloud sincerely. Let's contribute to the development of China's cloud computing industry together.<br />
<h3>Features</h3>
&nbsp;&nbsp;●&nbsp;<b>Resource partition</b>: physical machine resource is organized and managed by the unit of partition, which supports multiple application modes and resource usage modes.<br />
&nbsp;&nbsp;●&nbsp;<b>Resource leasing</b>: virtual and physical machines leasing is supported by the unit of cluster to achieve resource provisioning on demand.<br />
&nbsp;&nbsp;●&nbsp;<b>Application encapsulation</b>: make virtual appliances (virtual machine image template) through the web portal to help encapsulation and fast deployment of applications.<br />
&nbsp;&nbsp;&nbsp;&nbsp;More features to be released
<h3>Architecture</h3>
<img alt="LingCloud Architecture" src="<%=basePath%>/images/lingcloud-arch-en.png" height="344px" width="604px"><br /><br />
<%} %>
<!-- InstanceEndEditable -->
