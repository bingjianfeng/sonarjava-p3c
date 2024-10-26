class UserInfoDaoReg implements UserInfoDao { // Noncompliant {{【UserInfoDaoReg】是Dao或Service接口的实现，类名应以Impl结尾}}

}

class UserInfoDaoImpl implements UserInfoDao { // Compliant

}