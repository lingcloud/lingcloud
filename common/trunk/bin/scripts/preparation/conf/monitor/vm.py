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

descriptors = list()
vm_list = list()
vm_infos = list()

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

def get_vm_infos(name):
	global vm_list
	global vm_infos

	ret = ''
	c = 0
	for i in vm_list:
		cmd = 'virsh dumpxml '+i[0]
		(stat, output) = commands.getstatusoutput(cmd)
		if c > 0:
			ret += ','
			c += 1
		ret += output

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
