import java.util.*;
import java.net.*;
import java.io.*;

/**
 * This class activates the native java Kerberos Authentication Handler.
 * Typically you would need to set extra parameters to the jvm and use external
 * configuration files, however this class performs identically in pure code.
 * 
 * <p><strong>CAUTION</strong> : 
 * if file <code>%USER_HOME%\krb5cc_[username]</code> (kerberos TGT cache file) exists, it takes precedence over <code>LSA</code> (windows TGT cache).
 * <br />If the TGT cache file is outdated then kerberos will fail !
 * <br />In this case, either (1) remove the cache file, or (2) regenerate it with the <code>kinit</code> command provided by java (jre/bin)
 * </p>
 *
 * <p><strong>CAUTION</strong> : 
 * windows blocks access to TGT from <code>LSA</code> until you allow it explicitly in the registry. 
 * <br />Set the following key :
 * <br /><code>[HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\Control\Lsa\Kerberos\Parameters]
 * "allowtgtsessionkey"=dword:00000001</code>
 * </p>
 */
public class Kerberos
{
	/**
	 * Enables Kerberos authentication
	 * @param realm the kerberos realm (usually your Active Directory domain name, you can retrieve it using the <code>klist</code> command)
	 * @param kdc the host name of the KDC server (use the <code>nltest /dsgetdc:YOUR.DOMAIN.NAME</code> command to retrieve it)
	 */
	public static void enable(String realm, String kdc)
	{
		registerB64Scheme();
		
		String login_conf = "com.sun.security.jgss.krb5.initiate { " + 
			"com.sun.security.auth.module.Krb5LoginModule " + 
			"required " + 
			"renewTGT=true " + 
			"doNotPrompt=true " + 
			"useTicketCache=true; " + 
			"};";
		System.setProperty("java.security.krb5.realm", realm);
		System.setProperty("java.security.krb5.kdc", kdc);
		System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");
		System.setProperty("java.security.auth.login.config", "b64://" + Base64.getEncoder().encode(login_conf.getBytes("UTF-8")));
	}
	
	private static void registerB64Scheme()
	{
		try
		{
			new URL("b64://YQ==");
		}
		catch (final MalformedURLException e)
		{
			// SET DATA URI SUPPORT USING "b64://" SCHEME
			URL.setURLStreamHandlerFactory(new URLStreamHandlerFactory()
			{
				public URLStreamHandler createURLStreamHandler(String protocol)
				{
					return "b64".equals(protocol) ? new URLStreamHandler()
					{
						protected URLConnection openConnection(URL url) throws IOException
						{
							return new URLConnection(url)
							{
								public void connect() throws IOException
								{
								}
							   
								public InputStream getInputStream() throws IOException
								{
									return new ByteArrayInputStream(Base64.getDecoder().decode(url.getHost()+url.getPath()));
								}
							};
						}
					} : null;
				}
			});
		}
	}
}
