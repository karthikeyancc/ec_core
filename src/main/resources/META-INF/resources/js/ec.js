var app = angular.module("app", ['ngRoute', 'ngAnimate', 'ngMaterial','ngMessages',  'ui.grid', 'ui.grid.selection', 'ui.grid.exporter','ui.grid.pinning', 'ui.grid.pagination',  'ui.grid.cellNav','ec.http','ui.bootstrap']);
	app.config(
            function ($routeProvider) {
                $routeProvider. when('/home', { templateUrl: 'roleFiles/html/home.html'
                }).when('/:page', {
                    templateUrl: function(routeParams){
                    return 'html/'+routeParams.page+'.html';
                    }
                }).otherwise({redirectTo: '/home'});
            }
            
    )
    .config(
            function($mdDateLocaleProvider) {
              $mdDateLocaleProvider.formatDate = function(d) {
                  if(d){
                      var y=d.getFullYear();
                      var m=d.getMonth()+1;
                      var d=d.getDate();
                      var s= y+'-'+(m<10?'0':'')+m+'-'+(d<10?'0':'')+d;
                      return s;
                  }
                  return '';
               };
            })
    .controller('mainctrl', ['$scope','ec.http',
        function ($scope,ecHttp) {
        $scope.mods=[];
        $scope.user={};
        ecHttp.getArray('roleFiles/json/modules.json',{},$scope.mods);
        ecHttp.getObject('ru',{"qu":"whoami","limit":"1"},$scope.user);
    }]).controller('ecctrl', ['$scope', 'ec.http', '$location', function ($scope, ecHttp, $location) {
        $scope.queryParams=$location.search();
    }]).controller('lovctrl', ['$scope', 'ec.http', '$location','$timeout' ,function ($scope, ecHttp, $location,$timeout) {
        $scope.queryParams=$location.search();
        var qu="companycid",qup={},fieldname='ID,Name',fieldsize='50,150';
        $scope.dgrid={onRegisterApi:function (gridApi) { 
            $scope.dgrid.gridApi = gridApi;
            $scope.dgrid.gridApi.cellNav.on.navigate($scope,function(newRowCol, oldRowCol){
                $scope.dgrid.gridApi.selection.selectRow(newRowCol.row.entity);
            });
            $timeout( function() {
                $scope.dgrid.gridApi.cellNav.scrollToFocus($scope.dgrid.data[0],$scope.dgrid.columnDefs[0]);
                $scope.dgrid.gridApi.selection.selectRow($scope.dgrid.data[0]);
            }, 2);
        },
                enterPress:function(e){
                    console.log(e);
                    if(e.keyCode==13){
                        console.log('copying...');
                    }
                },
                   data:[], minRowsToShow:8, multiSelect : false,    enableRowSelection: true, enableRowHeaderSelection: false, enableColumnMenus:false 
        };
        $scope.sample=function(){
                var sendparam=angular.merge({},$location.search(),qup,{"qu": qu});
                ecHttp.getOptimalArray("r", sendparam, $scope.dgrid.data,$scope.render);
                return true;
        };
        $scope.render=function(d,f,t){
            if(!$scope.dgrid.columnDefs){
                $scope.dgrid.columnDefs=[];
                var i=0;
                var fn=fieldname.split(',');
                var fs=fieldsize.split(',');
                
                fn.forEach(function(fn){
                    $scope.dgrid.columnDefs.push(
                        { field:f[i],
                            displayName:fn,
                            width:fs[i],
                            pinnedLeft:i<2?true:false, 
                            type:t[i]=='INT'?'numeric':'string'});
                    console.log(t);
                    i++;
                });
            }
            ecHttp.showConfirmDialog({dgrid:$scope.dgrid},"Select Company",'<div id="dgrid" ui-grid="dgrid" ng-keypress="dgrid.enterPress($event)" ui-grid-cellNav ui-grid-pinning ui-grid-selection> </div>',null,$scope.setval,"OK","Cancel",null);
//             $timeout( function() {$scope.dgrid.gridApi.selection.selectRow($scope.dgrid.data[0]);}, 0, 1);
        };
        $scope.setval=function(b){
            console.log(b);
        };

    }]).controller('ecsearchinitctrl',['$scope','$mdDialog','$http','ec.http', function ($scope,$mdDialog,$http,ecHttp) {
        $scope.sbean={};
        $scope.open=function(qu,grid,title)
		{
            $scope.genSearchFields=function (fs,title){
                var fn;
                 var hs='<md-dialog aria-label="'+title+'" flex="flex">'+
                        ' <md-toolbar>'+
                        '<div class="md-toolbar-tools">'+
                        '<h2>'+title+'</h2><span flex></span>'+
                        '<md-button ng-click="cancel()" class="md-icon-button">'+
                        '<md-icon class="glyphicon glyphicon-remove"></md-icon>'+
                        '</md-button>'+
                        ' </div> </md-toolbar> <md-dialog-content><div style="margin:20px;" >';
                fs.forEach(function(f) {
                    fn=(f.alias?f.alias+'___':'')+f.field;
                    if (f.type == 'number' || f.type=='int') {
                        hs=hs+'<div class="form-group">'+
                            '<label >Start '+f.displayName+'</label>'+
                            '<input class="form-control" ng-model="sbean.start__'+fn+'"  type="text"/>'+
                            '</div>'+
                            '<div class="form-group">'+
                            '<label >End '+f.displayName+'</label>'+
                            '<input class="form-control" ng-model="sbean.end__'+fn+'"  type="text"/>'+
                            '</div>';
                    } else if (f.type == 'date') {
                        hs=hs+'<div class="form-group">'+
                                '<label for="ng-model="sbean.start__'+fn+'D">Start '+f.displayName+'</label>'+
                                '<md-datepicker class="form-control" ng-model="sbean.start__'+fn+'D" md-placeholder="Enter date" ></md-datepicker>'+
                                '</div> <div class="form-group">'+
                                '<label for="ng-model="sbean.end__'+fn+'D">End '+f.displayName+'</label>'+
                                '<md-datepicker class="form-control" ng-model="sbean.end__'+fn+'D" md-placeholder="Enter date" ></md-datepicker>'+
                                '</div> ';
                    } else if(f.type=='select')  {
                        hs=hs+'<div class="form-group">'+
                              '<label >'+f.displayName+'</label>'+
                              '<select class="form-control"  ng-model="sbean.'+fn+'">';
                              if(f.ecopts){
                                JSON.parse(f.ecopts).forEach(function(obj){
                                         var t=typeof obj;
                                         if(t=='object'){
                                             for(var v in obj){
                                                 hs=hs+'<option value="'+v+'">'+obj[v]+'</option>';
                                             }
                                         }else if (t=='string'){
                                                 hs=hs+'<option value="'+obj+'">'+obj+'</option>';
                                         }
                                     });                       
                              }
                              hs=hs+'</select></div>';
                    } else if(f.type!='nil')  {
                        hs=hs+'<div class="form-group">'+
                              '<label >'+f.displayName+'</label>'+
                              '<input class="form-control" ng-model="sbean.'+fn+'"  type="text"/>'+
                              '</div>';
                    }
                });
                hs=hs+'</div></md-dialog-content>'+
                    '<md-dialog-actions  layout="row"><span flex></span>'+
                    '<md-button class="btn btn-primary" ng-click="ok()">OK</md-button>'+
                    '<md-button class="btn btn-warning" ng-click="cancel()">Cancel</md-button>'+
                    '</md-dialog-actions></md-dialog>';
                return hs;
            };
            
            var conf=$mdDialog.confirm({
                template:$scope.genSearchFields($scope.$parent[grid].columnDefs,title),
                controller: function ($scope,$mdDialog){
                    $scope.cancel = function() {
                        $mdDialog.cancel();
                     };
                     $scope.ok = function() {
                         $mdDialog.hide(angular.copy($scope.sbean));
                     }
                 }
            });     ;
            $mdDialog.show(conf).then(
                    function(pa){
                        var size=0;
                        if(pa){
                            for (var key in pa){
                                    size++;
                                    
                                if(key &&key.endsWith('D') && (key.startsWith('start__')||key.startsWith('end__'))){
                                    var val= new Date(pa[key]);
                                    var ss=key.substring(0,key.length-1).replace('___','.');
                                    delete pa[key];
                                    pa[ss]=ecHttp.formatDate(val);
                                }
                                if(key.indexOf('___')>0){
                                    var val=pa[key];
                                    if(val){
                                        delete pa[key];
                                        pa[key.replace('___','.')]=val;
                                    }
                                }
                            }
                            if(size>0){
                                ecHttp.getOptimalArray("s", angular.merge(pa,{"qu": qu}),$scope.$parent[grid].data);
                            }
                        }
                    }, function(){}).finally(function(){conf=undefined});
		};
 }]).controller('gridctrl', ['$scope', 'uiGridConstants', 'ec.http',function ($scope,uiGridConstants,ecHttp) {
        $scope.ecgrid={
            enableFiltering: true,
            enableSorting: true,
            enableColumnHide: false,
            exporterPdfOrientation: 'portrait',
            exporterPdfPageSize: 'A0',
            gridMenuShowHideColumns: false,
            paginationPageSize: 250,
            enableColumnMenus:false,
            onRegisterApi:function (gridApi) { $scope.gridApi = gridApi; $scope.initFilters(this.columnDefs)},
            data: []
        };
        $scope.initFilters=function(defs){
            defs.forEach(function(f) {
                if(f.type=='number' ){
                    f. filters= [ { condition: uiGridConstants.filter.GREATER_THAN, placeholder: 'From' },
                        { condition: uiGridConstants.filter.LESS_THAN, placeholder: 'To' }];
                } else if(f.type=='string'|| (!f.type) ||(!f.type=='nil')){
                    f.filter={
                        condition: function(searchTerm, cellValue) {
                            return ecHttp.searchCellValue(searchTerm,cellValue);
                        }
                    };
                }
            });
        };
        
        $scope.matchTables=function(qu1,qp1,qu2,qp2,finalvar,filters){
                var user=[];
                var vacancy=[];
                filterfunc=function(d,n,t){
                    vacancy=d;
                    var ul=user.length;
                    var vl=vacancy.length;
                    vnames=n;
                    finalvar.length=0;
                    var u=0,v=0,s=true;
                    for(u=0;u<ul;u++){
                        for(v=0;v<vl;v++){
                            s=true;
                            for (f in filters){
                                if(s){
                                    s=ecHttp.searchCellValue(user[u][f],vacancy[v][filters[f]]);
                                }
                            }
                            if(s){
                                finalvar.push(angular.merge({},user[u],vacancy[v]));
                            }
                        }
                    }
                };
                var initfunc=function(d,n,t){
                    ecHttp.fetchOptimalArray("r",angular.merge({},qp2,{"qu":qu2}),0,500,vacancy,filterfunc);
                }
            ecHttp.fetchOptimalArray("r",angular.merge({},qp1,{"qu":qu1}),0,500,user,initfunc);

        };
        $scope.addOk=function (retbean){
        	ecHttp.writeObject("w",angular.merge({id:$scope.ecgrid.addedit.addTaskid,mode:"1",data:retbean}),function(){alert($scope.ecgrid.addedit.addSuccessMessage);},function(){alert($scope.ecgrid.addedit.addErrorMessage);});
        };
        $scope.addRecord=function(){
        	var inputbean={};
        	inputbean[$scope.ecgrid.addedit.beanName]={};
        	ecHttp.showConfirmDialog(inputbean,$scope.ecgrid.addedit.addDialogCaption,null,$scope.ecgrid.addedit.addFieldsTemplate,$scope.addOk,'Add','Cancel',$scope.ecgrid.addedit.beanName);
        };
        $scope.editOk=function (retbean){
        	ecHttp.writeObject("w",angular.merge({id:$scope.ecgrid.addedit.editTaskid,mode:"1",data:retbean}),function(){alert($scope.ecgrid.addedit.editSuccessMessage);},function(){alert($scope.ecgrid.addedit.editErrorMessage);});
        };
        $scope.editRecord=function(idfield){
        	var row=$scope.gridApi.selection.getSelectedRows();
        	if(row.length<1){
        		alert('Please select a record');
        		return ;
        	}
        	var sendbean = {};
        	sendbean[idfield]=row[0][idfield];
        	ecHttp.initbean(sendbean,$scope.ecgrid.addedit.recordDataQuery,sendbean,"r",function(bean){
        		var editinput={};
        		editinput[$scope.ecgrid.addedit.beanName]=bean;
        		ecHttp.showConfirmDialog(editinput,$scope.ecgrid.addedit.editDialogCaption+" - "+bean[idfield],null,$scope.ecgrid.addedit.editFieldsTemplate,$scope.editOk,'Edit','Cancel',$scope.ecgrid.addedit.beanName);
        	});
        };

}]).controller('gridmenuctrl', ['$scope', '$controller','ec.http','uiGridConstants', '$location',function ($scope, $controller,ecHttp,uiGridConstants,$location) {
        angular.extend(this, $controller('gridctrl', {$scope: $scope}));
        angular.extend($scope.ecgrid,{
            enableGridMenu: true,
            enableRowSelection: true,
            enableSelectAll: false,
            multiSelect: false
            
        });
        $scope.reRoute =function(route,field,params){
            ecHttp.reRouteFromGrid($scope.gridApi,function(param){$location.search(param).path(route);},field,params);
        };

}]).controller('ecdatectrl', ['$scope','$element','$attrs','$log','ec.http',function ($scope,$element,$attrs,$log,ecHttp) {
            $scope.ectoday=new Date();
            try{
                var mddpel=$element.find('md-datepicker')[0];
                if(mddpel.attributes['required']){
                var lble= angular.element(angular.element(mddpel).parent().find('label')[0]);
                if(lble){
                    var lbl_txt=lble.text();
                    if(lbl_txt && lbl_txt.trim().indexOf('*')<1){
                        lble.text(lbl_txt.trim()+' *');
                    }
                }
            }
        var v=mddpel.attributes['ng-model'].value;
        var vp=v.substring(0,v.length-1);
        var cbean=$scope;
        var cpbean=$scope.$parent;
        var ind=v.indexOf('.');
        if(ind>0){
            var bean=v.substring(0,ind);
            cbean=$scope[bean];
            cpbean=$scope.$parent[bean];
            v=v.replace(bean+'.','');
            vp=vp.replace(bean+'.','');
        }
            if(cpbean[vp]){
                var dval=ecHttp.ensureDate(cpbean[vp]);
            cbean[v]=dval;
            }
            }catch(e){
                       $log.log('date init error '+e);
             }
             
        $scope.$watch(bean+'.'+v, function(newValue, oldValue) {
            try{
                var d=cbean[v];
                d=ecHttp.ensureDate(d);
                if(d){
                    cpbean[vp]=ecHttp.formatDate(d);
                }
            }catch(e){
                $log.log('date set error'+e);
            }
        });
        $scope.$watch(bean+'.'+vp, function(newValue, oldValue) {
           try{
            var d=cbean[v];
            var dp=cbean[vp];
            if(!d&&dp){
                cbean[v]=new Date(dp);
            }
           }catch(e){
                $log.log('date watch error'+e);   
            }
        });

    }]);

var ecHttp = angular.module('ec.http', ['ngRoute','ngAnimate']);

ecHttp.factory('ec.http', ['$http','$httpParamSerializer','$route','$routeParams','$window','$location','$log','$mdDialog',function($http,$httpParamSerializer,$route,$routeParams,$window,$location,$log,$mdDialog){
	var shared={};
    function decodeOptimalData(servdata){
		var ret=[];
		var d=servdata['data'];
		var cns=servdata.colnames;
		var tns=servdata.type;
                var dt;
		for(r in d){
			var row={};
			for(c in cns){
				row[cns[c]]=d[r][c];
                                if(tns[c].startsWith("DATE")||tns[c]=="TIMESTAMP"){
                                    if(d[r][c]){
                                        try{
                                        dt=d[r][c].split(' ');
                                        row[cns[c]]=dt[0];
                                        }catch(e){$log.error(e)}
                                    }
                                }
			}
			ret.push(row);
		}
		return ret;
	}
	function parseDate(d){
                if(d){
                if(typeof d == "string"){
                    try{
                        var ds=d.split('-');
                        d=new Date(parseInt(ds[0]),parseInt(ds[1]-1),parseInt(ds[2]));
                        
                    }catch(e){
                        $log.log('date handling error '+e);
                    }
                }
             }
                return d;
        }
    function processOptimalArray(servdata,url,p,start,limit,myvar,cbfunc){
        var ret=decodeOptimalData(servdata);
        var rl=ret.length;
        if(start==0){
            myvar.length=0;
        }
        for (var i=0; i<rl; i++){
            myvar.push(ret[i]);
        }                    
        if(ret.length==limit){
            fac.fetchOptimalArray(url,p,start+limit,limit,myvar,cbfunc);
        }else if(cbfunc!=null){
            cbfunc(myvar,servdata.colnames,servdata.type);
        }
    }
    function successimpl(resp){
        if(resp.data && resp.data.error){
            var ind=resp.data.error.lastIndexOf('ception:');
            if(ind>0){
                alert(resp.data.error.substring(ind+8));
            }else{
                alert(resp.data.error);
            }
        }else if (resp.data.result){
                alert(resp.data.result);
                if(resp.data.reloadURL){
                    window.location.href=resp.data.reloadURL;
                    return;
                }
                if(resp.data.shared){
                    shared=resp.data.shared;
                }
                if(resp.data.forwardURL){
                    if(resp.data.forwardURL=='-1'){
                                 $window.history.back();
                    }else if(resp.data.forwardURL!=$location.path()){
                        $location.path(resp.data.forwardURL);
                    }else{
                        $route.reload();
                    }
                }
        }else{
        }
    }
    function errorimpl(resp){
        if(resp.data){
            alert(resp.data);
        }else if(resp.status=-1){
            alert('Server not running');
        }else{
            alert(resp.status +" Error occurred");
        }
    }
    function processResponse(data,fuser,fdefault){
        if(document.getElementById('send')){
            document.getElementById('send').disabled=false;
        }
        if(fuser){
            fuser(data);
        }else{
            fdefault(data);
        }
    }
    var fac= {
        showConfirmDialog:function(scope,title,content,contentURL,okfunc,oklabel,cancellabel,retbean){

                function DialogCtrl($scope, $mdDialog,childscope) {
                    for (var key in childscope){$scope[key]=childscope[key];}
                        $scope.ok = function() {
                            $mdDialog.hide();
                           if(okfunc){
                               var ret=null;
                               if(retbean){
                                   ret=$scope[retbean];
                               }
                                okfunc(ret);
                            }
                        };
                        $scope.cancel = function() {
                            $mdDialog.cancel();
                        };
                }
            var hs='<md-dialog aria-label="'+title+'" flex="flex">'+
                        ' <md-toolbar>'+
                        '<div class="md-toolbar-tools">'+
                        '<h2>'+title+'</h2><span flex></span>'+
                        '<md-button ng-click="cancel()" class="md-icon-button">'+
                        '<md-icon class="glyphicon glyphicon-remove"></md-icon>'+
                        '</md-button>'+
                        ' </div> </md-toolbar> <md-dialog-content ><div style="margin:20px;" >'+
                        (contentURL?'<div ng-include="\''+contentURL+'\'"></div>':content)+
                        '</div></md-dialog-content>'+
                    '<md-dialog-actions  layout="row"><span flex></span>'+
                    '<md-button class="btn btn-primary" ng-click="ok()">'+(oklabel?oklabel:'OK')+'</md-button>';
                    if(cancellabel){
                        hs+='<md-button class="btn btn-warning" ng-click="cancel()">'+cancellabel+'</md-button>';
                    }
                    hs+='</md-dialog-actions></md-dialog>';
                  $mdDialog.show({template:hs,controller:DialogCtrl,locals: { childscope: scope }});

        },
        successfunc:function(resp){
            successimpl(resp,this);
        },
        errorfunc:function(resp){
            errorimpl(resp);
        },
        ensureDate:function(resp){
            return parseDate(resp);
        }, searchCellValue:function(searchTerm,cellValue){
            if(!searchTerm || searchTerm.length<1){ return true; }
            if(!cellValue||cellValue.length<1){ return false; }
                var cv=cellValue.toLowerCase()+'';
                var svl = searchTerm.toLowerCase().split(',');
                var svl_len=svl.length;
                var res;
                var ret=false;
                for (var v=0;v<svl_len;v++){
                    if(svl[v]){
                        res=cv.indexOf(svl[v].trim());
                        if(res>=0){
                            ret= true;
                        }
                    }
                }
                return ret;
        },
        formatDate:function(d){
                if(d){
                      var y=d.getFullYear();
                      var m=d.getMonth()+1;
                      var d=d.getDate();
                      var s= y+'-'+(m<10?'0':'')+m+'-'+(d<10?'0':'')+d;
                      return s;
                  }
                  return '';
        },
		getShared:function()
		{
			return shared;
		},
		setShared:function(s)
		{
			shared=s;
		},
		getArray:function(url,p,myvar)
		{
			$http.get(url,{params:p}).success(
				function(resp){
					myvar.length=0;
					angular.extend(myvar,resp);
				}
			);
		},
		getOptimalArray:function(url,p,myvar,cbfunc)
		{
			$http.get(url,{params:p}).success(
				function(servdata){
                                    var ret=decodeOptimalData(servdata);
					myvar.length=0; 
                                        angular.extend(myvar,ret);
                                        if(cbfunc!=null){
                                            cbfunc(ret,servdata.colnames,servdata.type);
                                        }
				}
			);
		},
		fetchOptimalArray:function(url,p,start,limit,myvar,cbfunc)
		{
            p['start']=start;
            p['limit']=limit;
            $http.get(url,{params:p}).success(
                function(servdata){
                    processOptimalArray(servdata,url,p,start,limit,myvar,cbfunc);
                }
            );
		},
		getObject:function(url,p,myvar)
		{
			$http.get(url,{params:p}).success(
				function(servdata){
					for (var key in myvar){delete myvar[key];}
					angular.merge(myvar,servdata);
				}
			);
		},
		appendObject:function(url,p,myvar)
		{
			$http.get(url,{params:p}).success(
				function(servdata){
					angular.merge(myvar,servdata);
				}
			);
		},uploadFiles:function(u,d,sfunc,efunc)
		{ 
                            var payload = new FormData();
                            for (var key in d){
                                payload.append(key,d[key]);
                            }
                $http({ method: 'POST', url: u, headers: { 'Content-Type':undefined},transformRequest: angular.identity, data:payload })
                    .then(function(data){ processResponse(data,sfunc,successimpl);}, function(data){processResponse(data,efunc,errorimpl);});
        },writeObject:function(u,d,sfunc,efunc)
		{
                $http({ method: 'POST', url: u, headers: {'Content-Type': 'application/x-www-form-urlencoded'}, data:$httpParamSerializer(d) })
                    .then(function(data){ processResponse(data,sfunc,successimpl);}, function(data){processResponse(data,efunc,errorimpl);});
        },initbean:function(bean,qu,par,url,cbfunc){
            var sendparam={};
			sendparam=angular.merge(sendparam,$location.search(),par,{"qu": qu});
            $http.get(url?url:"r", {params: sendparam}).success(
                    function (servdata) {
                        var i = 0;
                        if(!servdata||!servdata.colnames){
                                return ;
                        }                        
                        servdata.colnames.forEach(function (cn) {
                            var dv = servdata.data[0][i];
                            if(servdata.type[i].toUpperCase().startsWith("DATE")){
                                try{
                                    if(dv){
//                                     var ds=dv.split('-');
//                                     var d1=new Date(parseInt(ds[0]),parseInt(ds[1])-1,parseInt(ds[2]));
//                                     bean[cn+'D']=d1;
                                    bean[cn]=dv;
                                    }
                                }catch(e){
                                     $log.log('date split and set error'+e);
                                }
                            }else{
                                if(dv!=null &&dv!="null"&dv!="undefined"){
                                    bean[cn] = dv;
                                }else{
                                    bean[cn] =null;
                                }
                            }
                            i++;
                        });
                        if(cbfunc!=null){
                            cbfunc(bean);
                        }
                    })
        },reRouteFromGrid:function(gridApi,fn,field,params){
            if(gridApi&&gridApi.selection){
                var row=gridApi.selection.getSelectedRows();
                var param={};
                if(params){
                    angular.merge(param,params);
                }
                if(field){
                    var v= (typeof field).toLowerCase();
                    if(v=='object'||v=='array'){
                        field.forEach(function(fi){param[fi]=row[0][fi];});
                    }else{
                        param[field]=row[0][field];
                    }
                }
                fn(param);
            }else{
                alert('Please select a row');
            }
        
        }
	};
    return fac;
}]).directive('submitPane',['ec.http','$mdDialog','$routeParams','$log',function(ecHttp,$mdDialog,$routeParams,$log){
	return {
		template: '<button class="btn btn-primary" id="send">{{caption}}</button>',
		restrict : 'E',
		scope : {
			beans : "=?",
			grids : "=?",
			formid : "@?",
			caption : "@",
            id: "@",
			mode :"@",
            url:"@?",
                        checkfunc:"&?",
                        successfunc:"&?",
                        errorfunc:"&?"
		},
		link: function(scope, element,controller,attributes) {
                    	element.bind('click', function(event) {
                            
                            var proceed=true;               
                            if(scope.checkfunc!=null){
                                var res=scope.checkfunc();
                                if(res!=null){
                                    var rt= typeof res;
                                    if("boolean"==rt){
                                        if(!res){
                                            proceed=false;
                                        }
                                    }else{
                                        alert('validate function returns '+rt+', it should return boolean');
                                        proceed=false;
                                    }
                                }else{
                                    alert('validate function returns null, it should return boolean');
                                    proceed=false;
                                }
                            }
                            if(!proceed){
                                return;
                            }
//                             $log.log(scope.formid);
//                             $log.log(scope.$parent[scope.formid]);
		        if(scope.formid && scope.$parent[scope.formid] &&(! scope.$parent[scope.formid].$valid)){
                            try{
                            var fse=document.querySelectorAll('form[name="'+scope.formid+'"]')[0];
                                var fs=angular.element(fse).scope()[scope.formid];
//                                 $log.log(fs);
                                var ret=true;
                                fse.querySelectorAll('input ,select,textarea').forEach(function (el){
//                                 $log.log(el);
//                                 $log.log(el.getAttribute("name"));
//                                 $log.log(fs[el.getAttribute("name")]);
                                    if(fs[el.getAttribute("name")] && fs[el.getAttribute("name")].$invalid){
                                        angular.element(el).addClass('mark-error');
                                        ret=false;
                                        return ;
                                    }
                                });
                                if(!ret){
                                    return ret;
                                }
                            }catch(e){
                                $log.log('mandatory field check error'+e);
                            }
                            return false;
                            }
              try{
				var sendata={};
                if(scope.beans){
                    scope.beans.forEach(function(bean){
                        angular.merge(sendata,scope.$parent[bean]);
                    });
                }
                if(scope.grids){
                    var gl=scope.grids.length;
                    for(var ggl=0;ggl<gl;ggl++){
                        var grid=scope.grids[ggl];
                        var gridscope=angular.element(document.querySelector('#'+grid)).scope();
                        var excludes=gridscope[grid].excludeProperties;
                        var gdata=gridscope[grid].data;
                        if(gridscope[grid].enableRowSelection&& gridscope.gridApi){
                                if(gridscope.gridApi.selection){
                                    gdata=gridscope.gridApi.selection.getSelectedRows();
                                    if(gdata.length<1&&gridscope[grid].checkSelected){
                                        alert('Please select a row');
                                        return false;
                                    }
                                }else if(gridscope[grid].checkSelected){
                                    alert('Please select a row');
                                    return false;
                                }
                            }
                            sendata[grid+'_length']=gdata.length;
                        gdata.forEach(function(rd){
                            var row=angular.copy(rd);
                            excludes.forEach(function(exc){
                                delete row[exc];
                            });
                            for (c in row){
                                if(row.hasOwnProperty(c)){
                                    var col=row[c];
                                    var sd=sendata[c];
                                    if(!sd){
                                        sd=[];
                                        sendata[c]=sd;
                                    }
                                    sd.push(col);
                                }
                            }
                        });
                        
                    };
                }

                var u="w";
                if(scope.url){
                    u=scope.url;
                }
                              event.target.disabled=true;

				ecHttp.writeObject(u,angular.merge({id:scope.id,mode:scope.mode,data:sendata},$routeParams),scope.successfunc,scope.errorfunc);
            }catch(e){
                event.target.disabled=false;
            }  
            });
		}
	};
}]).directive('initBean',['ec.http','$location',function(ecHttp,$location){
	return {
		restrict : 'E',
		scope : {
			bean : "@",
			qu : "@",
            url:"@?",
            params : "@?"
		},
		link: function(scope) {
            if(!scope.$parent[scope.bean]){
                scope.$parent[scope.bean]={};
            }
            var p=scope.params?JSON.parse(scope.params):{};
            ecHttp.initbean(scope.$parent[scope.bean],scope.qu,p,scope.url);
        }
	};
}]).directive('initGrid',['ec.http','$location',function(ecHttp,$location){
	return {
		restrict : 'E',
		scope : {
			grid : "@",
                        qu : "@",
                        limit:"@?"
		},
		link: function(scope) {
                    var pa=$location.search();
                    pa['qu']=scope.qu;
                    if(scope.grid){
                        if(scope.limit){
                                ecHttp.fetchOptimalArray("r",pa,0,parseInt(scope.limit),scope.$parent[scope.grid].data);
                        }else{
                                ecHttp.getOptimalArray("r",pa,scope.$parent[scope.grid].data);
                        }
                    }
                }
            
        };
    }]).directive('ecopturl',['ec.http','$http','$location','$log',function(ecHttp,$http,$location,$log){
	return {
		restrict : 'A',
		scope : {
			ecoptquery : "@?",
			ecoptlist : "@",
            ecopturl:"@"
		},
		link: function(scope,element,attrs) {
            if(!scope.$parent[scope.ecoptlist]){
                scope.$parent[scope.ecoptlist]=[];
            }
            if(attrs.required){
                var lbl= document.querySelectorAll('label[for="'+attrs.name+'"]');
                if(lbl){
                    var lble=angular.element(lbl);
                    var lbl_txt=lble.text();
                    if(lbl_txt && lbl_txt.trim().indexOf('*')<1){
                        lble.text(lbl_txt.trim()+' *');
                    }
                }
            }
            var elist=[];
            var pa=$location.search();
            if(scope.ecoptquery){
                pa['qu']=scope.ecoptquery;
            }
            $http.get(scope.ecopturl?scope.ecopturl:"r",{params:pa}).success(
                            function(data){
                                scope.$parent[scope.ecoptlist]=[];
                                data.data.forEach(function(obj){
                                    var t=Object.prototype.toString.call(obj) ;
                                    if(t =='[object Object]'){
                                        for(var v in obj){
                                            scope.$parent[scope.ecoptlist].push({"val":v,"disp":obj[v]});
                                        }
                                    }else if (t =='[object Array]'){
                                        scope.$parent[scope.ecoptlist].push({"val":obj[0],"disp":obj[1]});
                                    }else if (t ==='[object String]'){
                                            scope.$parent[scope.ecoptlist].push({"val":obj,"disp":obj});
                                    }
                                });
                            }
                        );
        }
	};
    }]).directive('ecopts',function(){
	return {
		restrict : 'A',
		scope : {
			ecopts : "@",
			ecoptlist : "@"
		},
		link: function(scope,element,attrs) {
            if(!scope.$parent[scope.ecoptlist]){
                scope.$parent[scope.ecoptlist]=[];
            }
            if(attrs.required){
                var lbl= document.querySelectorAll('label[for="'+attrs.name+'"]');
                if(lbl){
                    var lble=angular.element(lbl);
                    var lbl_txt=lble.text();
                    if(lbl_txt && lbl_txt.trim().indexOf('*')<1){
                        lble.text(lbl_txt.trim()+' *');
                    }
                }
            }
                JSON.parse(scope.ecopts).forEach(function(obj){
                    var t=typeof obj;
                    if(t=='object'){
                        for(var v in obj){
                            scope.$parent[scope.ecoptlist].push({"val":v,"disp":obj[v]});
                        }
                    }else if (t=='string'){
                            scope.$parent[scope.ecoptlist].push({"val":obj,"disp":obj});
                    }
                })
            }
        };
    }).directive('eclabel', function($compile) {
        return {
            restrict: 'A',
            scope: { eclabel: '@', name: '@' },
            priority: 10,
            link: function(scope,tElement, tAttrs, transclude) {
                var ecl=scope.eclabel;
                if(tAttrs.required && ecl && ecl.trim().indexOf('*')<1)
                {
                    ecl=ecl.trim()+' *';
                }
                var v=angular.element('<div class="form-group" ><label for='+scope.name+'>'+ecl+'</label></div>');
				tElement.addClass('form-control');
                tElement.after(v);
                v.append(tElement);    			
            }
        };
     }).directive('convertToNumber', function () {
    return {
        require: '?ngModel',
        link: function (scope, element, attrs, ngModel) {
            if (!ngModel)
            {
                return;
            } // do nothing if no ng-model
            ngModel.$parsers.push(function (val) {
                if(val){
                    return parseInt(val, 10);
                }else{
                    return val;
                }
            });
            ngModel.$formatters.push(function (val) {
                if (!ngModel)
                {
                    return;
                }
                return val? val+'':val;
            });
        }
    };
     }).directive('csv2array', function () {
    return {
        require: '?ngModel',
        link: function (scope, element, attrs, ngModel) {
            if (!ngModel)
            {
                return;
            } // do nothing if no ng-model
            ngModel.$parsers.push(function (val) {
                console.log(val);
                if(val){
                   
                    return val.toString();
                }else{
                    return val;
                }
            });
            ngModel.$formatters.push(function (val) {
                console.log(val);
                if (!ngModel)
                {
                    return;
                }
                if(val){
                     var t=val.split(',');
                    console.log(t);
                    return t;
                }else{
                    return;
                }
            });
        }
    };
    });
