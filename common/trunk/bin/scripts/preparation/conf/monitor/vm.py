#!/usr/bin/python
#
# Copyright (C) 2008-2011,
# LingCloud Team,
# Institute of Computing Technology,
# Chinese Academy of Sciences.
# P.O.Box 2704, 100190, Beijing, China.
#
# http://lingcloud.org
#

import commands
from xml.dom.ext.reader import Sax2
from xml import xpath,dom

descriptors = list()
vm_list = list()

gmetric = './gmetric -t string '

def get_vm_num(name):
	global vm_list

	cmd = 'virsh list'
	(stat, output) = commands.getstatusoutput(cmd)
	out_list = output.splitlines()[2:]
	vm_list = list()
	for i in out_list:
		l = i.split()
		if l[1] == 'Domain-0':
			continue
		vm_list.append(l)

	return len(vm_list)

def get_vm_cpuinfos(doc, domid):

	r = xpath.Evaluate('/domain/vcpu/text()', doc)
	ret = 'vcpu:' + r[0].nodeValue + ','
	cmd = 'virsh vcpuinfo ' + domid
	(stat, output) = commands.getstatusoutput(cmd)
	output = output[0:-1]
	ret += output.replace('\n', ',')
	
	return ret

def get_vm_meminfos(doc, domid):

	r = xpath.Evaluate('/domain/memory/text()', doc)
	ret = 'memory:' + r[0].nodeValue + ','
	r = xpath.Evaluate('/domain/currentMemory/text()', doc)
	ret += 'currMem:' + r[0].nodeValue
	
	return ret

def get_vm_diskinfos(doc, domid):
	ret = ''
	r = xpath.Evaluate('/domain/devices/disk/source/@file', doc)
	c = 0
	imgCmd = 'qemu-img info '
	for i in r:
		cmd = imgCmd + i.value
		(stat, output) = commands.getstatusoutput(cmd)
		c += 1
		if c > 1:
			ret += ';'
		ret += output.replace('\n', ',')

	return ret

def get_vm_netinfos(doc, domid):
	ret = ''
	r = xpath.Evaluate('/domain/devices/interface', doc)
	c = 0
	ifCmd = 'virsh domifstat ' + domid
	for i in r:
		c += 1
		if c > 1:
			ret += ';'
		dev = i.getElementsByTagName('target').item(0).getAttribute('dev')
		ret += 'dev:' + dev + ','
		ret += 'ip :' + i.getElementsByTagName('ip').item(0).getAttribute('address') + ','
		ret += 'mac:' + i.getElementsByTagName('mac').item(0).getAttribute('address') + ','
		cmd = ifCmd + ' ' + dev
		(stat, output) = commands.getstatusoutput(cmd)
		output = output[0:-1]
		output = output.replace(dev + ' ', '')
		output = output.replace(' ', ':')
		ret += output.replace('\n', ',')

	return ret

def get_vm_infos(name):
	global vm_list
	global gmetric

	ret = ''
	c = 0
	cpuinfos = ''
	meminfos = ''
	diskinfos = ''
	netinfos = ''
	vminfos = ''
	sep = ''
	for i in vm_list:
		cmd = 'virsh dumpxml '+i[0]
		(stat, domxml) = commands.getstatusoutput(cmd)
		doc = Sax2.FromXml(domxml)
		c += 1
		if c > 1:
			sep = '|'
		prefix = sep + "vmName:" + i[1] + ";"
		cpuinfos += prefix + get_vm_cpuinfos(doc, i[0])
		meminfos += prefix + get_vm_meminfos(doc, i[0])
		diskinfos += prefix + get_vm_diskinfos(doc, i[0])
		netinfos += prefix + get_vm_netinfos(doc, i[0])
		vminfos += sep + i[1] + ':' +i[2] 

	if c > 0:
		cmd = gmetric + ' -n "vm_cpu_infos" -v "' + cpuinfos + '"'
		# print cmd
		(stat, output) = commands.getstatusoutput(cmd)
		cmd = gmetric + ' -n "vm_mem_infos" -v "' + meminfos + '"'
		# print cmd
		(stat, output) = commands.getstatusoutput(cmd)
		cmd = gmetric + ' -n "vm_disk_infos" -v "' + diskinfos + '"'
		# print cmd
		(stat, output) = commands.getstatusoutput(cmd)
		cmd = gmetric + ' -n "vm_net_infos" -v "' + netinfos + '"'
		# print cmd
		(stat, output) = commands.getstatusoutput(cmd)
		cmd = gmetric + ' -n "vm_name_infos" -v "' + vminfos + '"'
		# print cmd
		(stat, output) = commands.getstatusoutput(cmd)

	return 'To be done' # ret # Ganglia python module's string length limited

def metric_init(params):
	'''Initialdize the random number generator and create the
	metric definition dictionary object for each metric.'''
	global descriptors
	
	d1 = {'name': 'vm_num',
		'call_back': get_vm_num ,
		'time_max': 90,
		'value_type': 'uint',
		'units': 'N',
		'slope': 'both',
		'format': '%u',
		'description': 'Virtual machine number',
		'groups': 'vm'}

	d2 = {'name': 'vm_infos',
		'call_back': get_vm_infos,
		'time_max': 90,
		'value_type': 'string',
		'units': '',
		'slope': 'both',
		'format': '%s',
		'description': 'Virtual machine informations',
		'groups': 'vm'}

	descriptors = [d1,d2]
	return descriptors

def metric_cleanup():
	'''Clean up the metric module.'''
	pass

#This code is for debugging and unit testing	
if __name__ == '__main__':
	params = {'RandomMax': '500',
		'ConstantValue': '322'}
	metric_init(params)
	for d in descriptors:
		v = d['call_back'](d['name'])
		print 'value for %s is %s' % (d['name'],  v)
	#print descriptors
