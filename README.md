# LingCloud

(Google Code URL: <https://code.google.com/archive/p/lingcloud/>)

LingCloud is a suite of cloud computing system software. LingCloud provides a resource single leasing point system for consolidated leasing physical and virtual machines, and supports various heterogeneous application modes including high performance computing, large scale data processing, massive data storage, etc. on shared infrastructure. LingCloud can help to build private clouds for governments, enterprises, schools, and research institutes, and it is also suitable to construct public clouds for managing data centers.

The main part of LingCloud is licensed under Apache License 2.0. We welcome any organizations or individuals dedicated to cloud computing to get involved in the development of LingCloud sincerely. Let's contribute to the development of cloud computing ecosystem.

## What Can LingCloud Do?

+ Resource partition
  - Physical machine resource is organized and managed by the unit of partition, which supports multiple application modes and resource usage modes

+ Resource leasing
  - Virtual and physical machines leasing is supported by the unit of cluster to achieve resource provisioning on demand

+ Application encapsulation
  - Make virtual appliances (virtual machine image template) through the web portal to help encapsulation and fast deployment of applications
  - More features to be released

## What Advantages does LingCloud Have?

+ Intensive
  - Virtual resource management to make full use of hardware
  - Coexistence of physical machines and virtual machines to ensure performance requirements
  - Multiple resource scheduling strategies for different optimizing goals

+ Efficient
  - Lease on demand, fast cluster deployment
  - Clusters to be automatically configured with required application environment, "One-Click Deployment"

+ Security
  - Ensure isolation of LingCloud from application systems at runtime
  - Global single sign-on
  - Real-time monitor for resource running state

+ Usability
  - Easy to learn and low cost for management and maintenance

## Publications

- `[ISPA '11]` Vega LingCloud: A Resource Single Leasing Point System to Support Heterogeneous Application Modes on Shared Infrastructure

```
@inproceedings{lu2011,
 author = {Lu, Xiaoyi and Lin, Jian and Zha, Li and Xu, Zhiwei},
 title = {{Vega LingCloud: A Resource Single Leasing Point System to Support Heterogeneous Application Modes on Shared Infrastructure}},
 booktitle = {{Proceedings of the 2011 IEEE Ninth International Symposium on Parallel and Distributed Processing with Applications}},
 series = {ISPA '11},
 pages = {99--106},
 year = {2011},
 publisher = {IEEE Computer Society},
 doi = {10.1109/ISPA.2011.58},
}
```

- `[FCS '13]` Consolidated Cluster Systems for Data Centers in the Cloud Age: a Survey and Analysis

```
@article{lin2013,
  author = {Lin, Jian and Zha, Li and Xu, Zhiwei},
  title = {{Consolidated Cluster Systems for Data Centers in the Cloud Age: a Survey and Analysis}},
  journal = {{Frontiers of Computer Science}},
  volume = {7},
  number = {1},
  pages = {1--19},
  year = {2013},
  publisher = {Springer},
  doi = {10.1007/s11704-012-2086-y},
}
```