#!/bin/bash

# ANSU 运输中介管理系统部署脚本

echo "=== ANSU 运输中介管理系统部署脚本 ==="

# 检查环境
echo "检查环境..."

# 检查Java
if ! command -v java &> /dev/null; then
    echo "错误: 未找到Java环境，请先安装Java 8+"
    exit 1
fi

# 检查Maven
if ! command -v mvn &> /dev/null; then
    echo "错误: 未找到Maven，请先安装Maven 3.6+"
    exit 1
fi

# 检查Node.js
if ! command -v node &> /dev/null; then
    echo "错误: 未找到Node.js，请先安装Node.js 16+"
    exit 1
fi

# 检查npm
if ! command -v npm &> /dev/null; then
    echo "错误: 未找到npm，请先安装npm"
    exit 1
fi

echo "环境检查通过！"

# 构建后端
echo "构建后端项目..."
cd ansu-api
mvn clean package -DskipTests
if [ $? -ne 0 ]; then
    echo "错误: 后端构建失败"
    exit 1
fi
cd ..

# 构建前端
echo "构建前端项目..."
cd ansu-h5
npm install
if [ $? -ne 0 ]; then
    echo "错误: 前端依赖安装失败"
    exit 1
fi

npm run build
if [ $? -ne 0 ]; then
    echo "错误: 前端构建失败"
    exit 1
fi
cd ..

echo "构建完成！"

# 创建部署目录
DEPLOY_DIR="ansu-deploy"
echo "创建部署目录: $DEPLOY_DIR"
mkdir -p $DEPLOY_DIR

# 复制后端jar包
cp ansu-api/target/ansu-api-*.jar $DEPLOY_DIR/ansu-api.jar

# 复制前端构建文件
cp -r ansu-h5/dist $DEPLOY_DIR/web

# 创建启动脚本
cat > $DEPLOY_DIR/start.sh << 'EOF'
#!/bin/bash

echo "启动 ANSU 运输中介管理系统..."

# 检查Java环境
if ! command -v java &> /dev/null; then
    echo "错误: 未找到Java环境"
    exit 1
fi

# 启动后端服务
echo "启动后端服务..."
nohup java -jar ansu-api.jar > ansu-api.log 2>&1 &
BACKEND_PID=$!
echo "后端服务已启动，PID: $BACKEND_PID"
echo $BACKEND_PID > ansu-api.pid

# 等待后端服务启动
echo "等待后端服务启动..."
sleep 10

# 检查后端服务是否启动成功
if curl -f http://localhost:8080/api/transport-orders/statistics > /dev/null 2>&1; then
    echo "后端服务启动成功！"
else
    echo "后端服务启动失败，请检查日志文件 ansu-api.log"
fi

# 启动前端服务（使用Python简单HTTP服务器）
if command -v python3 &> /dev/null; then
    echo "启动前端服务..."
    cd web
    nohup python3 -m http.server 8081 > ../ansu-web.log 2>&1 &
    WEB_PID=$!
    echo "前端服务已启动，PID: $WEB_PID"
    echo $WEB_PID > ../ansu-web.pid
    cd ..
    echo "前端服务地址: http://localhost:8081"
elif command -v python &> /dev/null; then
    echo "启动前端服务..."
    cd web
    nohup python -m SimpleHTTPServer 8081 > ../ansu-web.log 2>&1 &
    WEB_PID=$!
    echo "前端服务已启动，PID: $WEB_PID"
    echo $WEB_PID > ../ansu-web.pid
    cd ..
    echo "前端服务地址: http://localhost:8081"
else
    echo "警告: 未找到Python，无法启动前端服务"
    echo "请手动部署 web 目录到Web服务器"
fi

echo "系统启动完成！"
echo "后端API地址: http://localhost:8080"
echo "前端应用地址: http://localhost:8081"
echo ""
echo "默认用户:"
echo "  用户名: admin"
echo "  密码: 123456"
EOF

# 创建停止脚本
cat > $DEPLOY_DIR/stop.sh << 'EOF'
#!/bin/bash

echo "停止 ANSU 运输中介管理系统..."

# 停止后端服务
if [ -f ansu-api.pid ]; then
    BACKEND_PID=$(cat ansu-api.pid)
    if kill -0 $BACKEND_PID > /dev/null 2>&1; then
        kill $BACKEND_PID
        echo "后端服务已停止"
    else
        echo "后端服务未运行"
    fi
    rm -f ansu-api.pid
else
    echo "未找到后端服务PID文件"
fi

# 停止前端服务
if [ -f ansu-web.pid ]; then
    WEB_PID=$(cat ansu-web.pid)
    if kill -0 $WEB_PID > /dev/null 2>&1; then
        kill $WEB_PID
        echo "前端服务已停止"
    else
        echo "前端服务未运行"
    fi
    rm -f ansu-web.pid
else
    echo "未找到前端服务PID文件"
fi

echo "系统已停止"
EOF

# 设置执行权限
chmod +x $DEPLOY_DIR/start.sh
chmod +x $DEPLOY_DIR/stop.sh

# 创建配置说明文件
cat > $DEPLOY_DIR/README.txt << 'EOF'
ANSU 运输中介管理系统部署包

文件说明:
- ansu-api.jar: 后端服务jar包
- web/: 前端静态文件目录
- start.sh: 启动脚本
- stop.sh: 停止脚本

使用方法:
1. 确保已安装Java 8+和MySQL 8.0+
2. 创建数据库 ansu_transport
3. 修改 ansu-api.jar 中的数据库配置（如需要）
4. 运行 ./start.sh 启动系统
5. 运行 ./stop.sh 停止系统

访问地址:
- 后端API: http://localhost:8080
- 前端应用: http://localhost:8081

默认用户:
- 用户名: admin
- 密码: 123456

注意事项:
- 请确保8080和8081端口未被占用
- 首次启动会自动创建数据库表和示例数据
- 生产环境建议使用Nginx等Web服务器部署前端
EOF

echo "部署包已创建: $DEPLOY_DIR"
echo "请查看 $DEPLOY_DIR/README.txt 了解使用方法"
echo ""
echo "快速启动:"
echo "  cd $DEPLOY_DIR"
echo "  ./start.sh"
