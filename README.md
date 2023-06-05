# Garden System 2018
The garden system was initially implemented using standard Java Enterprise technologies (such as JavaFX/Swing, JSF web, EJB, and etc.), which followed a monolithic architecture. As the system complexity grew, I am currently undertaking the task of transforming it into a microservice-based architecture, specifically designed for mobile applications. My objective is to achieve a more decoupled and easily upgradable architecture, while also providing a modern front-end experience utilizing the Spring/Spring Boot and Angular/Ionic frameworks. <strong>This repository contains the majority of Garden's implementation which is deprecated and replaced by new implementation with new technologies, such as Spring Microservices, Angular, Ionic, and etc.</strong>

The following is the depecated monolithic architecture design of Garden system:

<img src="https://github.com/zhijun98/garden_2018/assets/9690419/bbd9f6e9-bad9-40f9-8a51-9598ccf0a307" width="600">

## Peony Office
PeonyOffice is a rich-client Java application which is supported by web services of RoseWeb. PeonyOffice uses of Netbeans platform embedded with JavaFX to present powerful GUI layer for remote users, i.e. accounting firms to process their daily business operations.

#### Employee Management
Support accounting firms' employee management which includes daily working progress report, timesheet, working activity logs, and privileges of using PeonyOffice.

<img src="https://github.com/zhijun98/garden_2018/assets/9690419/ab7034e2-a9ed-4371-b774-252e71a74702" width="600">

#### Job Dispatcher
Supprt managers assign and keep track of daily working assignments and communication with remote employees. Also, it has ability to follow up the current status of specific assignments (or jobs) to employees.

<img src="https://github.com/zhijun98/garden_2018/assets/9690419/daf5828f-f19f-4942-9b72-6ba923f86b3f" width="600">

#### Search Engine
A powerful search engine support company-wide's tax-case and document search. It uses of Amazon SNS service to send notifications to customers by SMS text messages. It also support to export search result into various format files, such as MS-words or PDF.

<img src="https://github.com/zhijun98/garden_2018/assets/9690419/acda0010-edb2-48d4-96b1-984eb8b513f2" width="600">

#### Talker
Peony Office provides an instant messenger, i.e. talker, so that employees may communicate with each other anytime and anywhere. It can keeps track of coversations for a limited-time period and search engine can also find the relevant conversation for specific taxation case.

<img src="https://github.com/zhijun98/garden_2018/assets/9690419/00a2665c-fb97-41c9-8cda-03ab3fa5dba5" width="600">

#### Daily Business Operations
Peony Office supports regular daily busienss operations such as taxation case report, internal working forum with historical logs, customized printing format, Email, etc.

<img src="https://github.com/zhijun98/garden_2018/assets/9690419/89bb2e3a-276c-4f02-8474-5ce5a45a9d01" width="600">

#### Archive and Backup/Restore
Peony Office provides a powerful document management utilites, achive the documents according to very customized way defined by users, build backup plans, schedule backups, and, in the worst case of original archived documents' crashing, Peony Office may restore everything.

<img src="https://github.com/zhijun98/garden_2018/assets/9690419/e20ef2c9-d9e6-4d22-b626-9d24d3ddc97a" width="600">

## Rose Web
RoseWeb is a standard business web portal which not only provide business offical web site but also provides web services endpoints to support the remote PeonyOffice execution. It was implemented with standard Java enterprise technologies, such as JSF, REST-WS, EJB, JPA, etc.

#### Online Business Management Site
Rose web serves not only for accounting business firms but also serve for their customers. According to the login credentials, Rose offers an online management on taxation cases for business or individual customers. For business, it may view, communicate, search its customers. For customers, they may checkout their taxation case status and communicate with the representatives of the accounting firms who take responsibility of their cases.

<img src="https://github.com/zhijun98/garden_2018/assets/9690419/aae2260b-b640-44a1-ac2c-368cfdfb8863" width="600">

#### Online Integrated Email for Business
For small business firms, one of headache daily problems is "email" which has no management at all. Rose web provides a centralized to manage their company's emails and associate emails with specific taxation cases. It will be presented in Peony Office also.

<img src="https://github.com/zhijun98/garden_2018/assets/9690419/d72f78d4-6b3a-49f2-8941-8f36474beed8" width="600">

#### Online CMS
Rose web is also a simplified content management system for business to maintain their web pages for the public, especially critical seasonal taxation news and business related knowledge-base.

<img src="https://github.com/zhijun98/garden_2018/assets/9690419/2b741539-6c8c-4396-abae-c39b946ae991" width="600">

## Documents
For more information on Garden system, refer to documents in this repo.
