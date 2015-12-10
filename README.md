# java-kerberos
Activate Kerberos Authentication without JVM arguments nor external config files required

This class activates the native java Kerberos Authentication Handler.
Typically you would need to set extra parameters to the jvm and use external
configuration files, however this class performs identically in pure code.

**CAUTION** : 

if file `%USER_HOME%\krb5cc_[username]` (kerberos TGT cache file) exists, it takes precedence over LSA (windows TGT cache).

If the TGT cache file is outdated then kerberos will fail !

In this case, either (1) remove the cache file, or (2) regenerate it with the kinit command provided by java (jre/bin)

**CAUTION** : 

windows blocks access to TGT from LSA until you allow it explicitly in the registry. 

Set the following key :

    [HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\Control\Lsa\Kerberos\Parameters]
    allowtgtsessionkey"=dword:00000001
