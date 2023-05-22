package org.openmrs.module.eptssync.problems_solver.engine;

import java.sql.SQLException;
import java.util.List;

import org.openmrs.module.eptssync.utilities.CommonUtilities;
import org.openmrs.module.eptssync.utilities.db.conn.DBConnectionInfo;
import org.openmrs.module.eptssync.utilities.db.conn.DBConnectionService;
import org.openmrs.module.eptssync.utilities.db.conn.DBException;
import org.openmrs.module.eptssync.utilities.db.conn.OpenConnection;

public class DatabasesInfo {
	
	public static String[] FGH_DB_NAMES = { "fgh_zam_alto_molocue_bonifacio_gruveta_m2",
											"fgh_zam_alto_molocue_nauela_m2",
											"fgh_zam_alto_molocue_sede_m2",
											"fgh_zam_derre_m2",
											"fgh_zam_gile_alto_ligonha_m2",
											"fgh_zam_gile_kayane_m2",
											"fgh_zam_gile_mamala_m2",
											"fgh_zam_gile_moneia_m2",
											"fgh_zam_gile_muiane_m2",
											"fgh_zam_gile_sede_m2",
											"fgh_zam_gile_uape_m2",
											"fgh_zam_gurue_lioma_m2",
											"fgh_zam_gurue_sede_m2",
											"fgh_zam_ile_mugulama_m2",
											"fgh_zam_ile_namanda_m2",
											"fgh_zam_ile_sede_m2",
											"fgh_zam_ile_socone_m2",
											"fgh_zam_inhassunge_bingagira_m2",
											"fgh_zam_inhassunge_cherimane_m2",
											"fgh_zam_inhassunge_gonhane_m2",
											"fgh_zam_inhassunge_olinda_m2",
											"fgh_zam_inhassunge_palane_m2",
											"fgh_zam_inhassunge_sede_m2",
											"fgh_zam_luabo_sede_m2",
											"fgh_zam_maganja_alto_mutola_m2",
											"fgh_zam_maganja_cabuir_m2",
											"fgh_zam_maganja_cariua_mapira_muzu_m2",
											"fgh_zam_maganja_mabala_m2",
											"fgh_zam_maganja_moneia_m2",
											"fgh_zam_maganja_muloua_m2",
											"fgh_zam_maganja_namurrumo_m2",
											"fgh_zam_maganja_nante_m2",
											"fgh_zam_maganja_sede_m2",
											"fgh_zam_maganja_vila_valdez_m2",
											"fgh_zam_milange_carico_m2",
											"fgh_zam_milange_chitambo_m2",
											"fgh_zam_milange_dachudua_m2",
											"fgh_zam_milange_dulanha_nambuzi_m2",
											"fgh_zam_milange_gurgunha_majaua_m2",
											"fgh_zam_milange_hr_m2",
											"fgh_zam_milange_liciro_m2",
											"fgh_zam_milange_muanhambo_mongue_m2",
											"fgh_zam_milange_sabelua_m2",
											"fgh_zam_milange_sede_m2",
											"fgh_zam_milange_tengua_m2",
											"fgh_zam_milange_vulalo_m2",
											"fgh_zam_mocuba_16junho_m2",
											"fgh_zam_mocuba_alto_benfica_m2",
											"fgh_zam_mocuba_chimbua_caiave_m2",
											"fgh_zam_mocuba_hd_m2",
											"fgh_zam_mocuba_intome_namabida_m2",
											"fgh_zam_mocuba_magogodo_m2",
											"fgh_zam_mocuba_muanaco_m2",
											"fgh_zam_mocuba_muaquiua_muloi_m2",
											"fgh_zam_mocuba_mugeba_m2",
											"fgh_zam_mocuba_munhiba_mataia_m2",
											"fgh_zam_mocuba_namagoa_m2",
											"fgh_zam_mocuba_namanjavira_m2",
											"fgh_zam_mocuba_nhaluanda_m2",
											"fgh_zam_mocuba_padre_usera_m2",
											"fgh_zam_mocuba_pedreira_m2",
											"fgh_zam_mocuba_samora_machel_m2",
											"fgh_zam_mocuba_sede_m2",
											"fgh_zam_mocuba_sisal_m2",
											"fgh_zam_mocubela_bajone_m2",
											"fgh_zam_mocubela_gurai",
											"fgh_zam_mocubela_ilha_idugo",
											"fgh_zam_mocubela_maneia_m2",
											"fgh_zam_mocubela_missal",
											"fgh_zam_mocubela_naico_m2",
											"fgh_zam_mocubela_sede_m2",
											"fgh_zam_mocubela_tapata_m2",
											"fgh_zam_molumbo_corromana_m2",
											"fgh_zam_molumbo_namucumua_m2",
											"fgh_zam_molumbo_sede_m2",
											"fgh_zam_mopeia_chimuara_m2",
											"fgh_zam_mopeia_lualua_m2",
											"fgh_zam_mopeia_sede_m2",
											"fgh_zam_morrumbala_cumbapo_m2",
											"fgh_zam_morrumbala_megaza_m2",
											"fgh_zam_morrumbala_mepinha_m2",
											"fgh_zam_morrumbala_pinda_m2",
											"fgh_zam_morrumbala_sede_m2",
											"fgh_zam_namacurra_furquia_m2",
											"fgh_zam_namacurra_macuse_m2",
											"fgh_zam_namacurra_malei_m2",
											"fgh_zam_namacurra_mbaua_m2",
											"fgh_zam_namacurra_mixixine_m2",
											"fgh_zam_namacurra_muceliua_m2",
											"fgh_zam_namacurra_muebele_m2",
											"fgh_zam_namacurra_mugubia_m2",
											"fgh_zam_namacurra_mutange_m2",
											"fgh_zam_namacurra_sede_m2",
											"fgh_zam_nicoadala_amoro_m2",
											"fgh_zam_nicoadala_domela_m2",
											"fgh_zam_nicoadala_ilalane_m2",
											"fgh_zam_nicoadala_licuare_m2",
											"fgh_zam_nicoadala_namacata_m2",
											"fgh_zam_nicoadala_quinta_girassol_m2",
											"fgh_zam_nicoadala_sede_m2",
											"fgh_zam_pebane_7abril_m2",
											"fgh_zam_pebane_alto_maganha_m2",
											"fgh_zam_pebane_impaca_m2",
											"fgh_zam_pebane_magiga_m2",
											"fgh_zam_pebane_malema_m2",
											"fgh_zam_pebane_mulela_m2",
											"fgh_zam_pebane_muligode_m2",
											"fgh_zam_pebane_naburi_m2",
											"fgh_zam_pebane_pele_pele_m2",
											"fgh_zam_pebane_sede_m2",
											"fgh_zam_pebane_tomea_m2",
											"fgh_zam_quelimane_17setembro_m2",
											"fgh_zam_quelimane_24julho_m2",
											"fgh_zam_quelimane_4dezembro_m2",
											"fgh_zam_quelimane_chabeco_m2",
											"fgh_zam_quelimane_coalane_m2",
											"fgh_zam_quelimane_hgq_m2",
											"fgh_zam_quelimane_icidua_m2",
											"fgh_zam_quelimane_inhangulue_m2",
											"fgh_zam_quelimane_ionge_m2",
											"fgh_zam_quelimane_madal_m2",
											"fgh_zam_quelimane_malanha_m2",
											"fgh_zam_quelimane_maquival_rio_m2",
											"fgh_zam_quelimane_maquival_sede_m2",
											"fgh_zam_quelimane_marrongane_m2",
											"fgh_zam_quelimane_micajune_m2",
											"fgh_zam_quelimane_namuinho_m2",
											"fgh_zam_quelimane_sangariveira_m2",
											"fgh_zam_quelimane_varela_m2",
											"fgh_zam_quelimane_zalala_m2"};
	
	public static String[] ICAP_DB_NAMES_NAMPULA = { "icap_nam_1demaio",
													"icap_nam_25setembro",
													"icap_nam_adppmuzuane",
													"icap_nam_akumi",
													"icap_nam_alua",
													"icap_nam_anchilo",
													"icap_nam_angoche",
													"icap_nam_barragem",
													"icap_nam_carapira",
													"icap_nam_chalaua",
													"icap_nam_corrane",
													"icap_nam_ilha_de_mocambique",
													"icap_nam_iuluti",
													"icap_nam_lalaua",
													"icap_nam_lumbo",
													"icap_nam_malema",
													"icap_nam_maratane",
													"icap_nam_marrere",
													"icap_nam_matibane",
													"icap_nam_meconta",
													"icap_nam_mecuburi",
													"icap_nam_micane",
													"icap_nam_moma",
													"icap_nam_monapo",
													"icap_nam_monapo_rio",
													"icap_nam_mossuril",
													"icap_nam_muecate",
													"icap_nam_muhalaexpansao",
													"icap_nam_murrupelane",
													"icap_nam_murrupula",
													"icap_nam_mutavarex",
													"icap_nam_mutuali",
													"icap_nam_nacala_porto",
													"icap_nam_nacalaporto",
													"icap_nam_nacalavelha",
													"icap_nam_nacaroa",
													"icap_nam_nacavala",
													"icap_nam_namaita",
													"icap_nam_namalala",
													"icap_nam_namapa",
													"icap_nam_nametil",
													"icap_nam_namialo",
													"icap_nam_namiconha",
													"icap_nam_namicopo",
													"icap_nam_namiepe",
													"icap_nam_namina",
													"icap_nam_namitoria",
													"icap_nam_nampula",
													"icap_nam_namucaua",
													"icap_nam_namutequeliua",
													"icap_nam_nanhuporio",
													"icap_nam_napipine",
													"icap_nam_natete",
													"icap_nam_niarro",
													"icap_nam_odinepa",
													"icap_nam_ontupaia",
													"icap_nam_pilivili",
													"icap_nam_psiquiatrico",
													"icap_nam_quissimanjulo",
													"icap_nam_rapale",
													"icap_nam_ribaue",
													"icap_nam_yapalamonapo"};
	
	
	public static String[] ARIEL_DB_NAMES_CD =	{	"ariel_cab_ancuabe",
														"ariel_cab_cs_cimento",
														"ariel_cab_cs_muxara",
														"ariel_cab_cs_natite",
														"ariel_cab_cs_ocua",
														"ariel_cab_cschiure_sede",
														"ariel_cab_csnakoto",
														"ariel_cab_csnamugelia",
														"ariel_cab_csnamunoi",
														"ariel_cab_cssamora_machel",
														"ariel_cab_eduardo_mondlane",
														"ariel_cab_hpp",
														"ariel_cab_hrmontepuez_20032023",
														"ariel_cab_hrmueda",
														"ariel_cab_mahate",
														"ariel_cab_mariri",
														"ariel_cab_mbuo",
														"ariel_cab_metoro",
														"ariel_cab_meza"};
		
	public static String[] ARIEL_DB_NAMES_MAPUTO = {"arie_map_cs_moamba_20230420",
													"ariel_map_20230420",
													"ariel_map_3_de_fevereiro",
													"ariel_map_bedene",
													"ariel_map_boquisso",
													"ariel_map_casagaiato",
													"ariel_map_chibucutso",
													"ariel_map_cssantos",
													"ariel_map_habeljafar",
													"ariel_map_hgm",
													"ariel_map_hpm",
													"ariel_map_liberdade",
													"ariel_map_machava2",
													"ariel_map_mafuiane",
													"ariel_map_magude",
													"ariel_map_mahelane",
													"ariel_map_mahubo",
													"ariel_map_maluana",
													"ariel_map_manhicasede",
													"ariel_map_maragra",
													"ariel_map_matogare",
													"ariel_map_matola2",
													"ariel_map_matuituine",
													"ariel_map_msantos",
													"ariel_map_muhalaze",
													"ariel_map_namaacha",
													"ariel_map_nwamatibjana",
													"ariel_map_pontadouro",
													"ariel_map_ressano_garcia",
													"ariel_map_salamanga",
													"ariel_map_tenga",
													"ariel_map_xinavane"};
	public static String[] EGPAF_DB_NAMES_GAZA = {  "egpaf_gz_25setembro",
													"egpaf_gz_altochangane",
													"egpaf_gz_banhine",
													"egpaf_gz_betula",
													"egpaf_gz_bungane",
													"egpaf_gz_celulamissavene",
													"egpaf_gz_chaimite",
													"egpaf_gz_changanine",
													"egpaf_gz_chiaquelane",
													"egpaf_gz_chibabelnovo",
													"egpaf_gz_chibondzane",
													"egpaf_gz_chibuto",
													"egpaf_gz_chicavane",
													"egpaf_gz_chicumbane",
													"egpaf_gz_chidenguele",
													"egpaf_gz_chilaulene",
													"egpaf_gz_chilembene",
													"egpaf_gz_chimbembe",
													"egpaf_gz_chimondzo",
													"egpaf_gz_chimundo",
													"egpaf_gz_chinhacanine",
													"egpaf_gz_chipadja",
													"egpaf_gz_chipenhe",
													"egpaf_gz_chissano",
													"egpaf_gz_chivongoene",
													"egpaf_gz_chokwe",
													"egpaf_gz_chongoene",
													"egpaf_gz_cocamissava",
													"egpaf_gz_combumune",
													"egpaf_gz_conhane",
													"egpaf_gz_cucuine",
													"egpaf_gz_cumba",
													"egpaf_gz_dengoine",
													"egpaf_gz_guija",
													"egpaf_gz_hokwe",
													"egpaf_gz_hpxx",
													"egpaf_gz_incadine",
													"egpaf_gz_incaia",
													"egpaf_gz_javanhane",
													"egpaf_gz_juliusnherere",
													"egpaf_gz_laranjeiras",
													"egpaf_gz_licilo",
													"egpaf_gz_lionde",
													"egpaf_gz_mabalane",
													"egpaf_gz_macasselane",
													"egpaf_gz_machua",
													"egpaf_gz_macia",
													"egpaf_gz_maciene",
													"egpaf_gz_macuacua",
													"egpaf_gz_macunene",
													"egpaf_gz_macupulane",
													"egpaf_gz_maivene",
													"egpaf_gz_malehice",
													"egpaf_gz_malhazine",
													"egpaf_gz_mamonho",
													"egpaf_gz_mangol",
													"egpaf_gz_mangundze",
													"egpaf_gz_manjacaze",
													"egpaf_gz_manjangue",
													"egpaf_gz_mapai",
													"egpaf_gz_mapapa",
													"egpaf_gz_maqueze",
													"egpaf_gz_marienngoabi",
													"egpaf_gz_massavasse",
													"egpaf_gz_massingir",
													"egpaf_gz_matsinhane",
													"egpaf_gz_mausse",
													"egpaf_gz_mazivila",
													"egpaf_gz_mbalavale",
													"egpaf_gz_meboi",
													"egpaf_gz_messano",
													"egpaf_gz_mpelane",
													"egpaf_gz_mubangoene",
													"egpaf_gz_mucotoene",
													"egpaf_gz_muianga",
													"egpaf_gz_muxaxane",
													"egpaf_gz_muzamane",
													"egpaf_gz_nalazy",
													"egpaf_gz_ndambine",
													"egpaf_gz_ndolene",
													"egpaf_gz_nhacutse",
													"egpaf_gz_nhamavila",
													"egpaf_gz_nhavaquene",
													"egpaf_gz_nwachicoluane",
													"egpaf_gz_olombe",
													"egpaf_gz_patricelumumba",
													"egpaf_gz_praiabilene",
													"egpaf_gz_praiaxaixai",
													"egpaf_gz_siaia",
													"egpaf_gz_tavane",
													"egpaf_gz_tuane",
													"egpaf_gz_unidade7",
													"egpaf_gz_urbano",
													"egpaf_gz_vilamilenio",
													"egpaf_gz_vladimirlenine",
													"egpaf_gz_xaixai",
													"egpaf_gz_zimilene",
													"egpaf_gz_zongoene",
													"egpaf_gz_zuza"};
	
	public static String[] CCS_DB_NAMES_MAPUTO = {	"ccs_map_albasine_m2",
													"ccs_map_alto_mae_m2",
													"ccs_map_bagamoio_m2",
													"ccs_map_catembe_m2",
													"ccs_map_chamanculo_m2",
													"ccs_map_cimento_m2",
													"ccs_map_hcmped_m2",
													"ccs_map_hpi_m2",
													"ccs_map_hulene_m2",
													"ccs_map_incassane",
													"ccs_map_inhaca_m2",
													"ccs_map_inhagoia_m2",
													"ccs_map_josemacamo_cs_m2",
													"ccs_map_josemacamohg_m2",
													"ccs_map_junho_m2",
													"ccs_map_magoanine_m2",
													"ccs_map_maio_m2",
													"ccs_map_malhangalene_m2",
													"ccs_map_mavalane_m2",
													"ccs_map_mavalanehg_m2",
													"ccs_map_mavalaneped_m2",
													"ccs_map_maxaquene_m2",
													"ccs_map_pescadores_m2",
													"ccs_map_polana_canico_m2",
													"ccs_map_porto_m2",
													"ccs_map_romao_m2",
													"ccs_map_xipamanine_m2",
													"ccs_map_zimpeto_m2",
													"css_map_magoanine_tendas_m2"};
											
	
	public static String[] ECHO_DB_MANICA = {	/*"echo_man_4_congresso_m2",
												"echo_man_amatongas_m2",
												"echo_man_bassane_m2",
												"echo_man_cabeca_velho_m2",
												"echo_man_catandica_m2",
												"echo_man_chaiva_m2",
												"echo_man_chigodole_m2",
												"echo_man_chipopopo_m2",
												"echo_man_chipudji_m2",
												"echo_man_chissui_m2",
												"echo_man_chitobe_m2",
												"echo_man_chiuala_m2",
												"echo_man_cruzamento_de_macossa_m2",
												"echo_man_dacata_m2",
												"echo_man_darue_m2",
												"echo_man_dombe_m2",
												"echo_man_espungabera_m2",
												"echo_man_garagua_m2",
												"echo_man_gondola_m2",
												"echo_man_gunhe_m2",
												"echo_man_guro_m2",
												"echo_man_honde_m2",
												"echo_man_hpc_m2",
												"echo_man_iac_m2",
												"echo_man_macate_m2",
												"echo_man_mandie_m2",
												"echo_man_manica_m2",
												"echo_man_marera_m2",
												"echo_man_matsinho_m2",
												"echo_man_mavende_m2",
												"echo_man_messica_m2",
												"echo_man_muda_serracao_m2",
												"echo_man_mude_m2",
												"echo_man_mupengo_m2",
												"echo_man_nhamaonha_m2",
												"echo_man_nhampassa_m2",
												"echo_man_nhassacara_m2",*/
												"echo_man_nhazonia_m2",
												/*"echo_man_pungue_sul_m2",
												"echo_man_save_m2",
												"echo_man_sdabril_m2",
												"echo_man_sembezea_m2",
												"echo_man_vanduzi_m2",
												"echo_man_vila_nova_m2",
												"echo_man_zembe_centro_m2"*/};
	
	public static String[] TESTING_DB = {"mozart_q2_fy23_consolidated"};
	
	private List<String> db_names;
	private DBConnectionInfo connInfo;
	private OpenConnection conn;
	private DBConnectionService connService;
	private String serverName;
	
	public DatabasesInfo(String serverName, List<String> db_names, DBConnectionInfo connInfo) {
		this.db_names = db_names;
		this.connInfo = connInfo;
		this.connService = DBConnectionService.init(this.connInfo);
		this.serverName = serverName;
	}
	
	public String getServerName() {
		return serverName;
	}
	
	public List<String> getDbNames() {
		return db_names;
	}
	
	public DBConnectionInfo getConnInfo() {
		return connInfo;
	}

	public synchronized OpenConnection acquireConnection() throws DBException {
		try {
			if (this.conn != null && this.conn.isValid(30000)) return this.conn;
		}
		catch (SQLException e) {
			throw new DBException(e);
		}
		
		this.conn = this.connService.openConnection();
		
		return this.conn;
	}
	
	
	public synchronized void finalizeConn() {
		if (this.conn != null) {
			this.conn.finalizeConnection();
		};
	}
	
	static String[] dbsQ3 = {	"openmrs_q3fy22_01_molocue_gruveta",
						"openmrs_q3fy22_01_molocue_nauela",
						"openmrs_q3fy22_01_molocue_sede",
						"openmrs_q3fy22_03_derre_sede",
						"openmrs_q3fy22_04_gile_alto_ligonha",
						"openmrs_q3fy22_04_gile_kayane",
						"openmrs_q3fy22_04_gile_mamala",
						"openmrs_q3fy22_04_gile_moneia",
						"openmrs_q3fy22_04_gile_muiane",
						"openmrs_q3fy22_04_gile_sede",
						"openmrs_q3fy22_04_gile_uape",
						"openmrs_q3fy22_05_gurue_lioma",
						"openmrs_q3fy22_05_gurue_sede",
						"openmrs_q3fy22_06_ile_mugulama",
						"openmrs_q3fy22_06_ile_namanda",
						"openmrs_q3fy22_06_ile_sede",
						"openmrs_q3fy22_06_socone",
						"openmrs_q3fy22_07_inhassunge_bingagira",
						"openmrs_q3fy22_07_inhassunge_cherimane",
						"openmrs_q3fy22_07_inhassunge_gonhane",
						"openmrs_q3fy22_07_inhassunge_mucula",
						"openmrs_q3fy22_07_inhassunge_olinda",
						"openmrs_q3fy22_07_inhassunge_sede",
						"openmrs_q3fy22_09_lugela_mulide",
						"openmrs_q3fy22_09_lugela_munhamade",
						"openmrs_q3fy22_09_lugela_namagoa",
						"openmrs_q3fy22_09_lugela_putine",
						"openmrs_q3fy22_09_lugela_sede",
						"openmrs_q3fy22_11_maganja_costa_alto_mutola",
						"openmrs_q3fy22_11_maganja_costa_cabuir",
						"openmrs_q3fy22_11_maganja_costa_cariua_mapira_muzo",
						"openmrs_q3fy22_11_maganja_costa_mabala",
						"openmrs_q3fy22_11_maganja_costa_moneia",
						"openmrs_q3fy22_11_maganja_costa_muloa",
						"openmrs_q3fy22_11_maganja_costa_namurumo",
						"openmrs_q3fy22_11_maganja_costa_nante",
						"openmrs_q3fy22_11_maganja_costa_sede",
						"openmrs_q3fy22_11_maganja_costa_vila_valdez",
						"openmrs_q3fy22_12_milange_carico",
						"openmrs_q3fy22_12_milange_chitambo",
						"openmrs_q3fy22_12_milange_dachudua",
						"openmrs_q3fy22_12_milange_dulanha",
						"openmrs_q3fy22_12_milange_gurgunha",
						"openmrs_q3fy22_12_milange_hr_milange",
						"openmrs_q3fy22_12_milange_liciro",
						"openmrs_q3fy22_12_milange_muanhambo",
						"openmrs_q3fy22_12_milange_sabelua",
						"openmrs_q3fy22_12_milange_sede",
						"openmrs_q3fy22_12_milange_tengua",
						"openmrs_q3fy22_12_milange_vulalo",
						"openmrs_q3fy22_13_mocuba_16_de_Junho",
						"openmrs_q3fy22_13_mocuba_alto_benfica",
						"openmrs_q3fy22_13_mocuba_caiave",
						"openmrs_q3fy22_13_mocuba_hd_mocuba",
						"openmrs_q3fy22_13_mocuba_intome",
						"openmrs_q3fy22_13_mocuba_magogodo",
						"openmrs_q3fy22_13_mocuba_mocuba_sisal",
						"openmrs_q3fy22_13_mocuba_muanaco",
						"openmrs_q3fy22_13_mocuba_mugeba",
						"openmrs_q3fy22_13_mocuba_muloi",
						"openmrs_q3fy22_13_mocuba_munhiba",
						"openmrs_q3fy22_13_mocuba_namagoa",
						"openmrs_q3fy22_13_mocuba_namanjavira",
						"openmrs_q3fy22_13_mocuba_nhaluanda",
						"openmrs_q3fy22_13_mocuba_padre_usera",
						"openmrs_q3fy22_13_mocuba_pedreira",
						"openmrs_q3fy22_13_mocuba_samora_machel",
						"openmrs_q3fy22_13_mocuba_sede",
						"openmrs_q3fy22_14_mocubela_bajone",
						"openmrs_q3fy22_14_mocubela_gurai",
						"openmrs_q3fy22_14_mocubela_ilha_idugo",
						"openmrs_q3fy22_14_mocubela_maneia",
						"openmrs_q3fy22_14_mocubela_missal",
						"openmrs_q3fy22_14_mocubela_naico",
						"openmrs_q3fy22_14_mocubela_sede",
						"openmrs_q3fy22_14_mocubela_tapata",
						"openmrs_q3fy22_15_molumbo_corromana",
						"openmrs_q3fy22_15_molumbo_namucumua",
						"openmrs_q3fy22_15_molumbo_sede",
						"openmrs_q3fy22_16_mopeia_chimuara",
						"openmrs_q3fy22_16_mopeia_lua_lua",
						"openmrs_q3fy22_16_mopeia_sede",
						"openmrs_q3fy22_17_morrumbala_cumbapo",
						"openmrs_q3fy22_17_morrumbala_megaza",
						"openmrs_q3fy22_17_morrumbala_mepinha",
						"openmrs_q3fy22_17_morrumbala_pinda",
						"openmrs_q3fy22_17_morrumbala_sede",
						"openmrs_q3fy22_19_namacurra_furquia",
						"openmrs_q3fy22_19_namacurra_macuse",
						"openmrs_q3fy22_19_namacurra_malei",
						"openmrs_q3fy22_19_namacurra_mbua",
						"openmrs_q3fy22_19_namacurra_muceliuia",
						"openmrs_q3fy22_19_namacurra_muebele",
						"openmrs_q3fy22_19_namacurra_mugubia",
						"openmrs_q3fy22_19_namacurra_mutange",
						"openmrs_q3fy22_19_namacurra_muxixine",
						"openmrs_q3fy22_19_namacurra_sede",
						"openmrs_q3fy22_22_qlm_04_dezembro",
						"openmrs_q3fy22_22_qlm_17_set",
						"openmrs_q3fy22_22_qlm_24_julho",
						"openmrs_q3fy22_22_qlm_chabeco",
						"openmrs_q3fy22_22_qlm_coalane",
						"openmrs_q3fy22_22_qlm_hospital_geral",
						"openmrs_q3fy22_22_qlm_icidua",
						"openmrs_q3fy22_22_qlm_inhangule",
						"openmrs_q3fy22_22_qlm_ionge",
						"openmrs_q3fy22_22_qlm_madal",
						"openmrs_q3fy22_22_qlm_malanha",
						"openmrs_q3fy22_22_qlm_maquival_rio",
						"openmrs_q3fy22_22_qlm_maquival_sede",
						"openmrs_q3fy22_22_qlm_marrongana",
						"openmrs_q3fy22_22_qlm_micajune",
						"openmrs_q3fy22_22_qlm_namuinho",
						"openmrs_q3fy22_22_qlm_sangarivela",
						"openmrs_q3fy22_22_qlm_varela",
						"openmrs_q3fy22_22_qlm_zalala",
						"openmrs_q3fy22_23_nicoadala_amoro",
						"openmrs_q3fy22_23_nicoadala_domela",
						"openmrs_q3fy22_23_nicoadala_ilalane",
						"openmrs_q3fy22_23_nicoadala_licuane",
						"openmrs_q3fy22_23_nicoadala_namacata",
						"openmrs_q3fy22_23_nicoadala_q_girassol",
						"openmrs_q3fy22_23_nicoadala_sede",
						"openmrs_q3fy22_24_pebane_7_abril",
						"openmrs_q3fy22_24_pebane_alto_maganha",
						"openmrs_q3fy22_24_pebane_impaca",
						"openmrs_q3fy22_24_pebane_magiga",
						"openmrs_q3fy22_24_pebane_malema",
						"openmrs_q3fy22_24_pebane_mulela",
						"openmrs_q3fy22_24_pebane_muligode",
						"openmrs_q3fy22_24_pebane_naburi",
						"openmrs_q3fy22_24_pebane_pele_pele",
						"openmrs_q3fy22_24_pebane_sede",
						"openmrs_q3fy22_24_pebane_tomea"};
	
		static String[] dbsQ4 = {"openmrs_ile_mugulama",
				"openmrs_ile_namanda",
				"openmrs_ile_sede",
				"openmrs_ile_socone",
				"openmrs_namacurra_mbaua",
				"openmrs_namacurra_muceliua",
				"openmrs_namacurra_muebele",
				"openmrs_namacurra_mutange",
				"openmrs_namacurra_sede",
				"openmrs_gurue_lioma",
				"openmrs_gurue_sede",
				"openmrs_inhassunge_bingagira",
				"openmrs_inhassunge_cherimane",
				"openmrs_inhassunge_gonhane",
				"openmrs_inhassunge_olinda",
				"openmrs_inhassunge_palane",
				"openmrs_inhassunge_sede",
				"openmrs_molumbo_corromana",
				"openmrs_molumbo_namucumua",
				"openmrs_molumbo_sede",
				"openmrs_quelimane_17setembro",
				"openmrs_quelimane_24julho",
				"openmrs_quelimane_4dezembro",
				"openmrs_quelimane_chabeco",
				"openmrs_quelimane_coalane",
				"openmrs_quelimane_hgq",
				"openmrs_quelimane_icidua",
				"openmrs_quelimane_inhangulue",
				"openmrs_quelimane_ionge",
				"openmrs_quelimane_madal",
				"openmrs_quelimane_malanha",
				"openmrs_quelimane_maquival_rio",
				"openmrs_quelimane_maquival_sede",
				"openmrs_quelimane_marrongane",
				"openmrs_quelimane_micajune",
				"openmrs_quelimane_namuinho",
				"openmrs_quelimane_sangariveira",
				"openmrs_quelimane_varela",
				"openmrs_quelimane_zalala",
				"openmrs_lugela_mulide",
				"openmrs_lugela_munhamade",
				"openmrs_lugela_namagoa",
				"openmrs_lugela_puthine",
				"openmrs_lugela_sede",
				"openmrs_molocue_bonifacio_gruveta",
				"openmrs_molocue_nauela",
				"openmrs_molocue_sede",
				"openmrs_mopeia_chimuara",
				"openmrs_mopeia_lualua",
				"openmrs_mopeia_sede",
				"openmrs_morrumbala_cumbapo",
				"openmrs_morrumbala_megaza",
				"openmrs_morrumbala_mepinha",
				"openmrs_morrumbala_pinda",
				"openmrs_morrumbala_sede",
				"openmrs_namacurra_macuse",
				"openmrs_nicoadala_amoro",
				"openmrs_nicoadala_domela",
				"openmrs_nicoadala_ilalane",
				"openmrs_nicoadala_licuar",
				"openmrs_nicoadala_namacata",
				"openmrs_nicoadala_quinta_girassol",
				"openmrs_nicoadala_sede",
				"openmrs_gile_alto_ligonha",
				"openmrs_gile_kayane",
				"openmrs_gile_mamala",
				"openmrs_gile_moneia",
				"openmrs_gile_muiane",
				"openmrs_gile_sede",
				"openmrs_gile_uape",
				"openmrs_maganja_alto_mutola",
				"openmrs_maganja_cabuir",
				"openmrs_maganja_cariua_mapira_muzo",
				"openmrs_maganja_mabala",
				"openmrs_maganja_moneia",
				"openmrs_maganja_muloa",
				"openmrs_maganja_namurrumo",
				"openmrs_maganja_nante",
				"openmrs_maganja_sede",
				"openmrs_maganja_vila_valdez",
				"openmrs_milange_carico",
				"openmrs_milange_chitambo",
				"openmrs_milange_dachudua",
				"openmrs_milange_dulanha_nambuzi",
				"openmrs_milange_hr",
				"openmrs_milange_liciro",
				"openmrs_milange_majaua_gurgunha",
				"openmrs_milange_muanhambo_mongue",
				"openmrs_milange_sabelua",
				"openmrs_milange_sede",
				"openmrs_milange_tengua",
				"openmrs_milange_vulalo",
				"openmrs_mocuba_16junho",
				"openmrs_mocuba_alto_benfica",
				"openmrs_mocuba_caiave_chimbua",
				"openmrs_mocuba_hd",
				"openmrs_mocuba_intome_namabida",
				"openmrs_mocuba_magogodo",
				"openmrs_mocuba_muanaco",
				"openmrs_mocuba_muaquiua_muloi",
				"openmrs_mocuba_mugeba",
				"openmrs_mocuba_munhiba_mataia",
				"openmrs_mocuba_namagoa",
				"openmrs_mocuba_namanjavira",
				"openmrs_mocuba_nhaluanda",
				"openmrs_mocuba_padre_usera",
				"openmrs_mocuba_pedreira",
				"openmrs_mocuba_samora_machel",
				"openmrs_mocuba_sede",
				"openmrs_mocuba_sisal",
				"openmrs_mocubela_bajone",
				"openmrs_mocubela_gurai",
				"openmrs_mocubela_ilha_idugo",
				"openmrs_mocubela_maneia",
				"openmrs_mocubela_missal",
				"openmrs_mocubela_naico",
				"openmrs_mocubela_sede",
				"openmrs_mocubela_tapata",
				"openmrs_namacurra_furquia",
				"openmrs_namacurra_malei",
				"openmrs_namacurra_mixixine",
				"openmrs_namacurra_mugubia",
				"openmrs_pebane_7abril",
				"openmrs_pebane_alto_maganha",
				"openmrs_pebane_impaca",
				"openmrs_pebane_magiga",
				"openmrs_pebane_malema",
				"openmrs_pebane_mulela",
				"openmrs_pebane_muligode",
				"openmrs_pebane_naburi",
				"openmrs_pebane_pele_pele",
				"openmrs_pebane_sede",
				"openmrs_pebane_tomea"};
	
		
		
	public static void main(String[] args) {
		CommonUtilities utilities = CommonUtilities.getInstance();
		
		List<String> q3 = utilities.parseArrayToList(dbsQ3);
		List<String> q4 = utilities.parseArrayToList(dbsQ4);
		
		for (String dbNameOnQ3: q3) {
			String[] hfOnQ3Parts = (dbNameOnQ3.split("openmrs_q3fy22_")[1]).split("_");
			
			String hfOnQ3 = "";
			
			for (int i =0; i < hfOnQ3Parts.length; i++) {
				if (i == 0) continue;
				
				if (i > 1) {
					hfOnQ3 += "_";
				}
				
				hfOnQ3 += hfOnQ3Parts[i];
			}
			
			
			boolean existsOnQ4 = false;
			
			for (String dbnameOnQ4 : q4) {
				if (dbnameOnQ4.contains(hfOnQ3)) {
					existsOnQ4 = true;
					break;
				}
			}
			
			if (!existsOnQ4) {
				System.out.println("NOT FOUND: " + dbNameOnQ3.toUpperCase());
			}
			else {
				System.out.println("Found");
			}
		}
	}
	
}

